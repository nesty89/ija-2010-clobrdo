/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.client;

import java.io.Serializable;

/**
 * Trida Packet zajistuje objekt pro komunikaci se serverem
 * @author nesty
 */
public class  Packet implements Serializable{
    protected int op;
    protected String gameName;
    protected int players;
    protected int fields;
    protected String XML;
    protected String msg;
    protected int playerId;
    
    /**
     * konstruktor
     * @param op typ paketu
     * @param gameName jmeno hry
     * @param players pocket hracu
     * @param fields pocet policek
     * @param XML data o poli
     * @param msg pripadna zprava
     * @param playerId identifikator hrace
     */
    public Packet(int op, String gameName, int players, int fields, String XML, String msg, int playerId)
    {
        this.op = op;
        this.gameName = gameName;
        this.players = players;
        this.fields = fields;
        this.XML = XML;
        this.msg = msg;
        this.playerId = playerId;
    }
    
    /**
     * metoda pro zisk poctu policek
     * @return pocet poli
     */
    public int getFields()
    {
        return this.fields;
    }
    
    /**
     * metoda pro zisk poctu hracu
     * @return pocet hracu
     */
    public int getPlayers()
    {
        return this.players;
    }
    
    /**
     * ziskani typu packetu operace
     * @return typ packetu
     */
    public int getOp()
    {
        return this.op;
    }
    
    /**
     * metoda vrati cislo hrace v ramci hry
     * @return cislo hrace
     */
    public int getPlayerId()
    {
        return this.playerId;
    }
    
    /**
     * meroda vraci jmeno hry
     * @return jmeno hry
     */
    public String getName()
    {
        return this.gameName;
    }
    
    /**
     * metoda vrati pripadnou zpravu
     * @return vrati zpravu 
     */
    public String getMsg()
    {
        return this.msg;
    }
    
    /**
     * metoda vraci data o hernim poli
     * @return data o hernim poli
     */
    public String getXML()
    {
        return this.XML;
    }
}