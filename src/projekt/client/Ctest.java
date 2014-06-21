/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.client;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor
 */
public class Ctest implements Serializable{

    protected static Socket s;
    final static int PORT = 8005;
    final static String HOST = "0.0.0.0";
    protected static ObjectOutputStream out;
    protected static ObjectInputStream in;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Ctest.s = new Socket(HOST, PORT);
            Ctest.out = new ObjectOutputStream(s.getOutputStream());
            Ctest.in = new ObjectInputStream(s.getInputStream());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Ctest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Ctest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Packet p = new Packet(4, "test", 2, 36, "testovaci projekt", "test",0);
        Packet p3 = new Packet(1, "test", 2, 36, "testovaci projekt", "test",1);
        try {
            out.reset();
            out.writeObject(p3);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Ctest.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true){
         try {
                        // Packet p2 = new Packet(0, gameName, -1, -1, XML, "OK", -1);
                         Packet p2 = (Packet) in.readObject();
                         if(p2 == null) continue;
                         else{
                             System.out.println("OK " + p2.getXML());
                             System.out.println("MSG " + p2.getMsg());
                                     break;}
                    } catch (IOException ex) {
                        System.out.println("ERR1");
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        System.out.println("ERR2");
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }
        }
        
        try {
            s.close();
            in.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Ctest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

}
