/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.server;

import java.io.*;

/**
 *
 * @author Igor
 */
public class FileWork {
    
    
    /**
     * nacte prenaseny soubor do retezce
     * @return string obsah souboru
     */
    public String loadDoc(String name) {
        FileInputStream fos = null;
        File f = null;

        try {
            f = new File(name + ".xml");
            fos = new FileInputStream(f);
        } catch(java.lang.Exception e) {
            System.err.println("chyba otvirani " + e);
            return "Chyba";
        }
        
        int znak;
        String s = "";
        try {
            while((znak = fos.read()) != -1 ) { // nacteni celehosoubor do retezce
                s += ((char) znak);
            }
            //System.out.println("loaded text: " + s);
        } catch(IOException e) {
            System.err.println("Chyba pri cteni souboru " + e);
            return "Chyba";
        }

        try {
            fos.close();
        } catch(java.lang.Exception e) {
            System.err.println("chyba zavirani " + e);
        }

        return s;
    }
    
     /**
     * ulozi retezec do souboru
     * @param s retezec k ulozeni
     * @return true/false
     */
    public boolean saveDoc(String s, String name) {
        FileOutputStream fos = null;
        File f = null;

        try {
            f = new File(name + ".xml");
            fos = new FileOutputStream(f);
        } catch(java.lang.Exception e) {
            System.err.println("chyba otvirani " + e);
            return false;
        }

        try {
            fos.write(String.valueOf(s).getBytes());
        } catch(java.io.IOException e) {
            System.err.println("chyba zapisu " + e);
            return false;
        }

        try {
            fos.close();
        } catch(java.lang.Exception e) {
            System.err.println("chyba zavirani " + e);
            return false;
        }
        return true;
    }
    
}
