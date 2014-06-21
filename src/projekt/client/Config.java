package projekt.client;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
//import javax.help.HelpSet;
//import javax.help.JHelp;
import java.net.URL;

/**
 * Trida config - uklada a nacita informace ulozene v konfiguracnim souboru
 * @author now
 */
public class Config {
    protected Frame frame;
    protected String filename = "config.xml";
    protected String fieldType = "typ2";
    protected String help = "help/helpset.hs";
    protected String title = "Člověče, nezlob se - IJA2011";
    protected String playerName = "Player1";

    /**
     * Konstruktor - nacte informace z konfiguracniho souboru
     * @param frame - objekt okna, kde nastavujeme hodnoty promennych
     */
    public Config(Frame frame) {
        this.frame = frame;
        this.loadConfig();
    }

    /**
     * nacte informace z konfiguracniho souboru
     */
    final void loadConfig() {
        Document document = null;
        File f = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            frame.printError("Nepovedlo se nacist config soubor.");
        }

        try {
            f = new File(this.filename);
        } catch(java.lang.Exception e) {
            frame.printError("Nepovedlo se otevrit config soubor.");
        }

        try {
            document = builder.parse(f);
        } catch (SAXException e) {
            frame.printError("Nepovedlo se nacist config soubor.");
        } catch (IOException e) {
            frame.printError("Nepovedlo se nacist config soubor.");
        }

        NodeList load = document.getElementsByTagName("config");
        NodeList nl = load.item(0).getChildNodes();
        // 1 fieldtype
        // 3 help
        // 5 title
        // 7 playername
        this.fieldType = nl.item(1).getChildNodes().item(0).getNodeValue();
        this.help = nl.item(3).getChildNodes().item(0).getNodeValue();
        this.title = nl.item(5).getChildNodes().item(0).getNodeValue();
        this.playerName = nl.item(7).getChildNodes().item(0).getNodeValue();
        //System.out.println(this.toString());
    }

    /**
     * ulozi aktualni nastaveni do konfiguracniho souboru config.xml
     */
    public void saveConfig(Frame a) {
        FileOutputStream fos = null;
        try {
            File f = new File(this.filename);
            fos = new FileOutputStream(f);
        } catch(java.lang.Exception e) {
            frame.printError("Nepovedlo se otevrit config soubor.");
        }

        String s = "<?xml version='1.0' encoding='utf-8' standalone='no' ?>\n";
        s = s + "<config>\n";
            s = s + "\t<fieldType>"+a.select.getSelectedItem()+"</fieldType>\n";
            s = s + "\t<help>" + this.help + "</help>\n";
            s = s + "\t<title>" + this.title + "</title>\n";
            s = s + "\t<playerName>" + a.name.getText() + "</playerName>\n";
        s = s + "</config>\n";

        try {
            fos.write(String.valueOf(s).getBytes());
        } catch(java.io.IOException e) {
            frame.printError("Nepovedlo se ulozit config soubor.");
        }

        try {
            fos.close();
        } catch(java.lang.Exception e) {
            frame.printError("Nepovedlo se ulozit config soubor.");
        }
    }


}
