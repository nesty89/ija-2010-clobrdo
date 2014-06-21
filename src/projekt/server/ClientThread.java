/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import projekt.client.Packet;

/**
 *
 * @author Igor
 * obsluha vlakna pro klienta
 */
public class ClientThread extends Thread implements Serializable, Runnable {
    
    protected Socket socket;
    protected String XML = "";
    protected String gameName = "";
    protected int player;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected int maxPlayers;
    protected FileWork fw;
    protected ArrayList<ClientThread> clients;
    
    /*
     * konstruktor
     * @param socket soket pro naslouchani
     * @param clients pole klientu
     */
    public ClientThread(Socket socket, ArrayList<ClientThread> clients)
    {
        super();
        this.clients = clients;
        this.fw = new FileWork();
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        //runThread();
    }
    
    /*
     * provede spusteni behu klienta
     */
    @Override
    public void run() 
    {
        //start();
        Packet p_in;
        while(true)
        {
            p_in = null;
            try {
               p_in = (Packet) in.readObject();
            } catch (IOException ex) {
                System.out.print("ERR1");
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                System.out.print("ERR2");
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            switch(p_in.getOp())
            {
                case 0:
                    System.out.println("new");
                    newGame(p_in);
                    break;
                case 1:
                    System.out.println("join");
                    joinGame(p_in);
                    break;
                case 2:
                    System.out.println("next");
                    nextRound(p_in);
                    break;
                case 3:
                    System.out.println("save");
                    saveGame(p_in);
                    break;
                case 4:
                    System.out.println("load");
                    loadGame(p_in);
                    break;
                case 5:
                    System.out.println("end");
                    endGame(p_in);
                    break;
                default:
                    notifyThis("Chyba: neznamy pozadavek");
                    break;
            }
        }
        
    }
    
    /*
     * odesle vsem klientum danne hry zpravu packet
     * @param p co se ma odeslat
     */
    public void BroadCast(Packet p)
    {
        System.out.println("broadcast");
        for(ClientThread clt : clients)
        {
            if(clt.gameName.equals(p.getName()) && clt != this ) // vsem v pripade chyby jinak ostatnim
            {
                try {
                    clt.out.reset();
                    clt.out.writeObject(p);
                    clt.out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /*
     * vytvori novou hru
     * @param p informace pro tvorbu hry
     */
    public void newGame(Packet p)
    {        
        System.out.println("in newgame");
        for(ClientThread i : clients)
        {
            if(i.gameName.equals(p.getName()) && i != this)
            {
                notifyThis("Chyba: Pozadovana hra jiz existuje");
                try {
                    this.out.close();
                    this.in.close();
                    this.socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                clients.remove(this);
                System.out.println("moc lidi");
                return;
            }
        }
        
        this.XML = p.getXML();
        System.out.println(XML);
        this.gameName = p.getName();
        this.maxPlayers = p.getPlayers();
        this.player = p.getPlayerId();
        System.out.println("players: " + maxPlayers);
        
        
        if(check(this.gameName,this.maxPlayers))
        {
            System.out.println("in check");
            Packet p2 = new Packet(0, gameName, maxPlayers, p.getFields(), XML, "", player);
            BroadCast(p2);
           // try {
           //     out.reset();
           //     out.writeObject(p2);
           //     out.flush();
           // } catch (IOException ex) {
           //     Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
           // }
            
        }
    }
    
    /*
     * vytvoreni pripojeni ke hre
     * @param p packet od zadatele s informacemy pro pripojeni
     */
    public void joinGame(Packet p)
    {
        System.out.println("in join");
        String xml = "";
        int max = 0;
        if(!checkPlayerId(p.getPlayerId(), p.getName()))
        {
            notifyThis("Chyba: hrac se stejnym poradovim cislem jiz ve hre existuje");
            try {
                this.out.close();
                this.in.close();
                this.socket.close();
                clients.remove(this);
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for(int j = 0; j < clients.size(); j++)
        {
            ClientThread i = clients.get(j);
            if(i.gameName.equals(p.getName()) && i != this)
            {
                xml = i.XML;
                max = i.maxPlayers;
                break;
            }
        }
        System.out.println("max: " + max);
        if(clientCounter(p.getName()) > max || xml.equals(""))
        {
            if(xml.equals(""))
            {
                notifyThis("Chyba: hra neexistuje");
            }
            else
            {
                notifyThis("Chyba: hra je plna");
                try {
                    this.out.close();
                    this.in.close();
                    this.socket.close();
                    clients.remove(this);
                } catch (IOException ex) {
                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else
        {
            this.XML = xml;
            this.gameName = p.getName();
            this.player = p.getPlayerId();
            this.maxPlayers = max;
        }
        
    }
    
    /*
     * odesle vsem informace o provedene zmene
     * @param p packet obsahujici informace o zmene
     */
    public void nextRound(Packet p)
    {
        System.out.println("in next");
        this.XML = p.getXML();
        Packet p2 = new Packet(0, gameName, -1, -1, p.getXML(), "", player);
        BroadCast(p2);
    }
    
    /*
     * ulozeni aktualni hry pod jejim jmenem
     * @param p packet zaslany uzivatelem pro ziskani jmena
     */
    public void saveGame(Packet p)
    {
        System.out.println("in save");
        if(!fw.saveDoc(p.getXML(),p.getName()))
            notifyThis("Chyba: ukladani souboru");
    }
    
    /*
     * nahraje hru
     * @param p informace potrebne pro nahrani hry zaslane uzivatelem
     */
    public void loadGame(Packet p)
    {
        System.out.println("in load");
        for(ClientThread i : clients)
        {
            if(i.gameName.equals(p.getName()) && i != this)
            {
                notifyThis("Chyba: Pozadovana hra jiz existuje");
                try {
                    this.out.close();
                    this.in.close();
                    this.socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                clients.remove(this);
            }
        }
        System.out.println("name: " + p.getName());
        this.XML = fw.loadDoc(p.getName());
        System.out.println("XML: " + XML);
        this.gameName = p.getName();
        this.player = p.getPlayerId();
        this.maxPlayers = p.getPlayers();
        System.out.println("players: " + maxPlayers);
        
        
        if(this.XML.equals("Chyba"))
        {
            notifyThis("Chyba: Nepodarilo se nacist pozadovanou hru, zkontrolujte nazev.");
            try {
                this.out.close();
                this.in.close();
                this.socket.close();
                clients.remove(this);
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(check(this.gameName,this.maxPlayers))
        {
            System.out.println("in check1");
            Packet p2 = new Packet(0, gameName, maxPlayers, p.getFields(), XML, "", player);
            BroadCast(p2);
            try {
                out.reset();
                out.writeObject(p2);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    /*
     * rozesle informaci o ukonceni hry
     * @param p prichozi paket ukoncujici hru
     */
    public void endGame(Packet p)
    {
        System.out.println("in end");
        Packet p2 = new Packet(2, gameName, -1, -1, "","Konec hry", player);
        BroadCast(p2);
        closeThreads(p.getName());
    }
    
    /*
     * odesle informaci o chybe na serveru a ukonci aktualni hru
     * @param p packet, ktery chybu vyvolal - ziskani infa pro odeslani
     */
    public void serverErr(Packet p)
    {
        Packet p2 = new Packet(1, p.getName(), -1, -1, "", "Chyba: Problem na serveru, hra bude ukoncena", player);
        BroadCast(p2);
        closeThreads(p.getName());
    }
    
    /*
     * zavre vsechny vlakna pro danou hru
     * @param name nazev hry 
     */
    public void closeThreads(String name)
    {
        for(ClientThread i : clients)
        {
            if(i.gameName.equals(name) && i != this) // TODO: dopsat broadcast
            {
                try {
                    clients.remove(i);
                    i.in.close();
                    i.out.close();
                    i.socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        try {
            clients.remove(this);
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * provede overeni zda je pripojen dostatecny pocet klientu
     * @param name jmeno hry
     * @param max pozadovnany pocet klinetu
     * @return true/false
     */
    public boolean check(String name, int max)
    {
        int count = 1;
        //System.out.println("in check");
        while(true)
        {
            System.out.println("in while check");
            for(int j = 0; j < this.clients.size(); j++)
            {
                ClientThread i = clients.get(j); 
                //System.out.println("in for check");
                if(i.gameName.equals(name) && i != this)
                    count++;
                //System.out.println(count);
                //System.out.println(max);
                if(count == max)
                {
                    System.out.println("return");
                    return true;
                }
            }
            count = 1;
        }
        
    }
    
    /*
     * provede soucet prave pripojenych klientu
     * @param name jmeno hry
     * @return pocet aktualne pripojenych klientu k danne hre
     */
    public int clientCounter(String name)
    {
        int count = 0;
        for(int j = 0; j<this.clients.size();j++)
        {
            ClientThread i = clients.get(j);
            if(i.gameName.equals(name))
                count++;
            //System.out.println("count: " + count);
        }
        //System.out.println("count2: " + count);
        return count;
    }
    
    /*
     * zasle aktualnimu klientu zpravu
     * @param msg zasilana zprava
     */
    public void notifyThis(String msg)
    {
        Packet p = new Packet(1, gameName, -1, -1, "", msg, player);
        try {
            out.reset();
            out.writeObject(p);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * kontrola jestli id hrace neni pro tuto hru obsazene
     * @param id poradove cislo hrace
     * @param name jmeno hrace
     * @return true/false
     */
    public boolean checkPlayerId(int id, String name)
    {
        for(ClientThread i : clients)
        {
            if(name.equals(i.gameName) && id == i.player && i != this)
                return false;
        }
        return true;
    }
}
