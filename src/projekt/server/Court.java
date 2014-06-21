

package projekt.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



 
/**
 *
 * @author now
 */
public class Court implements Serializable{
    
    final static int PORT = 8005;
    //final static String HOST = "merlin.fit.vutbr.cz";
    protected ServerSocket server;
    protected ArrayList<ClientThread> clients;
    
    public void run()
    {
        clients = new ArrayList<ClientThread>();
        try {
            server = new ServerSocket(PORT);
            while(true)
            {
                Socket s = server.accept();
                ClientThread clt = new ClientThread(s,clients);
                clients.add(clt);
                //System.out.println("POCET VLAKEN: " + clients.size());
                clt.start();
            }
        } catch (IOException ex) {
            System.err.println("Chyba serveru." + ex);
            killAll();
            System.exit(1);
        } finally
        {
            if(server != null)
            {
                killAll();
            }
            try {
                server.close();
            } catch (IOException ex) {
                killAll();
                System.err.println("Chyba serveru");
            }
        }
    }
    
    public void killAll()
    {
        for(ClientThread i : clients)
        {
            try {
                i.out.close();
                i.in.close();
                i.socket.close();
                server.close();
                clients.remove(i);
            } catch (IOException ex) {
                killAll();
                System.err.println("Chyba serveru");
            }
        }
    }
   

    

    
    public static void main(String[] args)
    {
       new Court().run(); 
    }
}
