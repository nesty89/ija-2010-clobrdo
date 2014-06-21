/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.client;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor
 */
public class ReadNet extends Thread implements Serializable{
 
    protected Frame f;
    public ReadNet(Frame f)
    {
        this.f = f;
        
        start();
    }
    
    @Override
    public void run()
    {
        reload();
    }
    public void reload()
    {
       
        f.loadFromXml(f.makeXml());
        return;
    }
    public void zomb()
    {
       try {
            this.sleep(10);
        } catch (InterruptedException ex) {
            Logger.getLogger(ReadNet.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    public void readIt()
    {
         
        while(f.round % f.players != f.netplayernum)
            {
            
                //System.out.println("here" + round);
                try {
                    Packet p2 = (Packet) f.in.readObject();
                    if(p2 == null) continue;
                    if(p2.op == 0)
                    {
                        //System.out.println(p.XML);                    
                        f.loadFromXml(p2.XML);
                    }
                    else
                    {
                        f.printError(p2.msg);
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        return;
    }
}
