package projekt.client;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.help.HelpSet;
import javax.help.JHelp;
import java.net.*;


/**
 * Trida Frame - zajistuje zobrazeni hraciho okna, generovani pole
 * @author now
 */
public class Frame extends JFrame implements ActionListener, MouseListener, Serializable {
    public JFrame frame;
    private SpringLayout rozmisteni;
    private SpringLayout contl;
    private JMenuBar menuBar;
    private JMenu menuFile = null;
    private JMenuItem menuItemNew = null;
    private JMenuItem menuItemSave = null;
    private JMenuItem menuItemSaveAs = null;
    private JMenuItem menuItemExit = null;
    private JMenu menuHelp = null;
    private JMenuItem menuItemHelp = null;
    private JMenuItem menuItemAbout = null;
    private JPanel content;
    final Border blackline = BorderFactory.createLineBorder(Color.black);
    protected int fieldWidth = 35;
    protected int fields;
    protected int players = 2;
    protected int human = 1;
    protected int poradi = 0;
    protected int round = 0;
    protected JLabel loskostkos;
    protected SpringLayout position;
    private JPanel infoPanel;
    private SpringLayout.Constraints infoPanelBounds;
   // private JLabel label1;
    private JPanel playerPanel;
    private SpringLayout.Constraints playerPanelBounds;
   // private JLabel label2;
    private JPanel iPanel;
    private SpringLayout.Constraints iPanelBounds;
    protected boolean turnDone = true;
    //private JLabel label3;
    private JPanel cubePanel;
    private SpringLayout.Constraints cubePanelBounds;
    protected int cubeValue = 7;
    protected int typeAI = 0;
    protected int value = -1;
    protected Config config;
    protected JFrame opt;
    protected JLabel info;
    public String fieldType;
    public JComboBox select;
    public JTextField name;
    private int portnum;
    private String hosturl;
    private int server = 0;
    private int netgame = 0;
    private String gamename = "";
    protected int netplayernum = 0; // cislo hrace pro sitovou  hru
    protected int PCAI = 0;
    protected boolean send = true;
    protected boolean lock = false;
    //SidePanel side;
    protected ArrayList<Field> pole;
    final String[] colors = new String[] {"s_blue","s_gray","s_green","s_orange","s_pink","s_purple","s_red","s_yellow"};
    protected String[] x = new String[] {"ff6","ff1","ff2","ff3","ff4","ff5"};  // obrazky
    protected String[] s = new String[]{"cube/ff6_cube","cube/ff1_cube","cube/ff2_cube","cube/ff3_cube","cube/ff4_cube","cube/ff5_cube"};
    protected String[] hrac = new String[]{"modrý","šedý","zelený","oranžový","růžový","fialový","černvený", "žlutý"};
    protected String[] log = new String[]{" - PC: defaultní inteligence", " - PC: nejdelší skok", " - PC: nasazení", "- PC: vyhození", "- PC: do domečku" };
    protected int[] ai;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
    Socket socket;

    /**
     * Kontruktor tridy
     * @param fields - pocet policek
     * @param players - pocet hracu
     * @param human - pocet lidskych hracu
     * @param pole - seznam obsahujici policka hraci desky
     */
    public Frame(int fields, int players, int human, ArrayList<Field> pole, int netgame, String hosturl, int portnum, int server, String gamename, int netplayernum) {
        config = new Config(this);

        this.fields = fields;
        this.players = players;
        this.human = human;

        this.netgame = netgame;
        this.hosturl = hosturl;
        this.portnum = portnum;
        this.server = server;
        this.gamename = gamename;
        this.netplayernum = netplayernum;
        this.loskostkos = new JLabel();
        this.info = new JLabel();
        this.loskostkos.setIcon(new ImageIcon("images/"+config.fieldType+"/cube/ff6_cube.jpg"));
        this.ai = new int[7];
        for(int i = 0; i < ai.length; i++)
        {
            ai[i] = 0;
        }
        if(pole == null)
            this.pole = new ArrayList<Field>(this.fields);
        else
            this.pole = pole;
        Collections.shuffle(Arrays.asList(x));  // zamichame obrazky
        for (int i = 0; i < 6; i++) {
            if(x[i].equals("ff6"))
            {
              x[i] = x[0];
              x[0] = "ff6";
              break;
            }
        }
        for (int i = 0; i < 6; i++) {  // zamichani i kostky ve stejnem poradi
            s[i] = "cube/"+x[i]+"_cube";
        }
       
        init();
    }

    /**
     * inicializace okna
     */
    public void init() {
        frame = new JFrame(config.title);  // vytvori okno se zadanym nadpisem
        Toolkit obraz = frame.getToolkit();
        Dimension velikost_okna = obraz.getScreenSize(); // nacte velikost okna
        frame.setBounds(velikost_okna.width/2-512, velikost_okna.height/2-410, 1010, 800); // otevre okno presne uprostred obrazovky
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {  // pri zavirani okna zavola metodu konec()
            @Override
            public void windowClosing(WindowEvent e) {
                konec();
            }
        });
//System.out.println(netgame+" "+hosturl+" "+portnum);
        showMenu(); // zobrazi menu
        rozmisteni = new SpringLayout(); // layout okna
        if(fields > 0 && players > 0) {
            if(netgame == 0)
            {
                generateGamePanel();
            }// vygeneruje hraci plochu
            if(netgame == 1) {
                human = players;
                try {
                    //System.out.println("soket");
                    socket = new Socket(hosturl, portnum);
                    //System.out.println("soket2");
                } catch (UnknownHostException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }

                if(portnum < 0 || portnum > 65535)
                    portnum = 80;
                try {
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.reset();
                    //System.out.println("out");
                } catch(Exception e) {
                    e.printStackTrace();
                }
                try {
                    //System.out.println("in");
                    in = new ObjectInputStream(socket.getInputStream());
                    //System.out.println("in");
                    //System.out.println(netgame);
                } catch (IOException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
                Packet p;
                 //System.out.println("free");
                if(server == 1)
                {
                    generateGamePanel();
                    this.netplayernum = 0;
                   // System.out.println("zdes");
                    p = new Packet(0,gamename,players,fields, makeXml(),"", netplayernum);
                    try {
                        out.reset();
                        out.writeObject(p);
                        out.flush();

                    } catch (IOException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if(server == 2)
                {
                 //   System.out.println("joint");
                    p = new Packet(1,gamename,-1,-1,"","",netplayernum);
                    try {
                        out.reset();
                        out.writeObject(p);
                        out.flush();
                    } catch (IOException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if(server == 3) // load
                {
                    this.netplayernum = 0;
                    p = new Packet(4,gamename,players,-1,"","",netplayernum);
                    try {
                        out.reset();
                        out.writeObject(p);
                        out.flush();

                    } catch (IOException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                while(true)
                {
                    if((netplayernum == round%players && server != 3) || server == 1) break;
                   // System.out.println("heredoc");
                    try {
                        // Packet p2 = new Packet(0, gameName, -1, -1, XML, "OK", -1);
                         Packet p2 = (Packet) in.readObject();
                         if(p2 == null) continue;
                         if(p2.op == 1)
                         {
                             printError(p2.msg);
                             break;
                         }
                         else if(p2.op == 0)
                         {
                             loadFromXml(p2.XML);
                             break;
                         }
                         /*while(round % players != netplayernum)
                         {
                             p2 = (Packet) in.readObject();
                             if(p2 == null) continue;
                             if(p2.op == 1)
                             {
                                 printError(p2.msg);
                                 break;
                             }
                             if(p2.op == 0)
                             {
                                 loadFromXml(p2.XML);
                             }
                             else
                             {
                                 printError(p2.msg);
                                 break;
                             }
                         }*/
                    } catch (IOException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
               /* while(round % players != netplayernum)
                {
                    try {
                        Packet p2 = (Packet) in.readObject();
                        if(p2 == null) continue;
                        if(p2.op == 0)
                        {
                            loadFromXml(p2.XML);
                        }
                        else
                        {
                            printError(p2.msg);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }*/

                //System.out.println("heress");
            }
            while((server == 2 || server == 3) && round % players != netplayernum)
                {
                    try {
                        Packet p2 = (Packet) in.readObject();
                        if(p2 == null) continue;
                        if(p2.op == 0)
                        {
                            loadFromXml(p2.XML);
                        }
                        else
                        {
                            printError(p2.msg);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        }


        //frame.setResizable(false);
        frame.setVisible(true);

    }

    /**
     * vytvori menu aplikace
     */
    public void showMenu() {
       menuBar = new JMenuBar();
            menuFile = new JMenu("Lokalni hra");
            menuFile.setMnemonic(KeyEvent.VK_S);
            menuBar.add(menuFile);

                menuItemNew = new JMenuItem("Nova hra");
                menuItemNew.setMnemonic(KeyEvent.VK_N);
                menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK)); // klavesova zkratka CTRL+N
                menuItemNew.addActionListener(this);
                menuFile.add(menuItemNew);

                menuItemNew = new JMenuItem("Nacist hru");
                menuItemNew.setMnemonic(KeyEvent.VK_T);
                menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK)); // klavesova zkratka CTRL+O
                menuItemNew.addActionListener(this);
                menuFile.add(menuItemNew);

                menuFile.addSeparator();  // oddelovac

                /*menuItemSave = new JMenuItem("Ulozit");
                menuItemSave.setMnemonic(KeyEvent.VK_U);
                menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK)); // klavesova zkratka CTRL+S
                menuItemSave.addActionListener(this);
                menuFile.add(menuItemSave);*/

                menuItemSaveAs = new JMenuItem("Ulozit jako");
                menuItemSaveAs.setMnemonic(KeyEvent.VK_J);
                menuItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)); // klavesova zkratka CTRL+SHIFT+S
                menuItemSaveAs.addActionListener(this);
                menuFile.add(menuItemSaveAs);

                menuFile.addSeparator();  // oddelovac

                menuItemExit = new JMenuItem("Konec");
                menuItemExit.setMnemonic(KeyEvent.VK_K);
                menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
                menuItemExit.addActionListener(this);
                menuFile.add(menuItemExit);

            menuFile = new JMenu("Sitova hra");
            menuFile.setMnemonic(KeyEvent.VK_S);
            menuBar.add(menuFile);

                menuItemNew = new JMenuItem("Nova hra");
                menuItemNew.setMnemonic(KeyEvent.VK_O);
                menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK)); // klavesova zkratka CTRL+T
                menuItemNew.addActionListener(this);
                menuFile.add(menuItemNew);

                menuItemSaveAs = new JMenuItem("Ulozit jako");
                menuItemSaveAs.setMnemonic(KeyEvent.VK_J);
                menuItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)); // klavesova zkratka CTRL+SHIFT+S
                menuItemSaveAs.addActionListener(this);
                menuFile.add(menuItemSaveAs);


            menuHelp = new JMenu("Napoveda");

            menuHelp = new JMenu("Moznosti");
            menuHelp.setMnemonic(KeyEvent.VK_N);
            menuBar.add(menuHelp);

                menuItemHelp = new JMenuItem("Nastaveni");
                menuItemHelp.setMnemonic(KeyEvent.VK_T);
                menuItemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
                menuItemHelp.addActionListener(this);
                menuHelp.add(menuItemHelp);

                menuItemHelp = new JMenuItem("Obsah napovedy");
                menuItemHelp.setMnemonic(KeyEvent.VK_H);
                menuItemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK));
                menuItemHelp.addActionListener(this);
                menuHelp.add(menuItemHelp);

                menuItemAbout = new JMenuItem("O Aplikaci");
                menuItemAbout.setMnemonic(KeyEvent.VK_O);
                menuItemAbout.addActionListener(this);
                menuHelp.add(menuItemAbout);

            frame.setJMenuBar(menuBar);// zobrazi menu
    }

    /**
     * vygeneruje hraci pole podle zadanych paranmetru v konstruktoru
     */
    public void generateGamePanel() {
        int rowFields = fields / 12;
        for(int i=1;i<=12;i++) // vygeneruje hraci pole
            generateRow(i, rowFields, x);

        for(int i=1;i<=12;i++) { // vygeneruje startovni domecky
            if(i % 3 != 0) {  // vse krome 3, 6, 9, 12
                if(this.fields > 36) { // jen vice nez 36 policek
                    generateStartHomes(i);
                } else if(i % 3 == 1) { // 1, 4, 7, 10
                    generateStartHomes(i);
                }
            }
        }

        for(int i=1;i<=12;i++) { // vygeneruje finalni domecky
            int startX = 0;
            int startY = 0;
            int posunX = 0;
            switch(this.fields) {
                case 72: posunX = 70;break;
                case 60: posunX = 122;break;
                case 48: posunX = 175;break;
                case 36: posunX = 227;break;
            }
            int posunY = 35;
            switch(i) {
                case 1: startX = (rowFields*fieldWidth+posunX); startY = posunY; break;
                case 2: startX = ((rowFields*2)*fieldWidth+posunX); startY = posunY; break;
                case 3: startX = ((rowFields*2)*fieldWidth+posunX); startY = (rowFields*fieldWidth+posunY); break;
                case 4: startX = ((rowFields*3)*fieldWidth+posunX); startY = (rowFields*fieldWidth+posunY); break;
                case 5: startX = ((rowFields*3)*fieldWidth+posunX); startY = ((rowFields*2)*fieldWidth+posunY); break;
                case 6: startX = ((rowFields*2)*fieldWidth+posunX); startY = ((rowFields*2)*fieldWidth+posunY); break;
                case 7: startX = ((rowFields*2)*fieldWidth+posunX); startY = ((rowFields*3)*fieldWidth+posunY); break;
                case 8: startX = (rowFields*fieldWidth+posunX); startY = ((rowFields*3)*fieldWidth+posunY); break;
                case 9: startX = (rowFields*fieldWidth+posunX); startY = ((rowFields*2)*fieldWidth+posunY); break;
                case 10: startX = posunX; startY = ((rowFields*2)*fieldWidth+posunY); break;
                case 11: startX = posunX; startY = (rowFields*fieldWidth+posunY); break;
                case 12: startX = (rowFields*fieldWidth+posunX); startY = (rowFields*fieldWidth+posunY); break;
            }

            if(i % 3 != 0) {  // vse krome 3, 6, 9, 12
                if(this.fields > 36) { // jen vice nez 36 policek
                    generateFinishHomes(startX, startY, i);
                } else if(i % 3 == 1) { // 1, 4, 7, 10
                    generateFinishHomes(startX, startY, i);
                }
            }
        }
        drawGamePanel();
    }

    /**
     * vykresli hraci pole
     */
    public void drawGamePanel() {
        SpringLayout.Constraints contentBounds = null;
        frame.getContentPane().removeAll();
        rozmisteni = new SpringLayout();
        frame.getContentPane().setLayout(rozmisteni);
        
        content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setBorder(blackline);
        content.setLayout(rozmisteni);
        contentBounds = rozmisteni.getConstraints(content);
        contentBounds.setWidth(Spring.constant(780));
        contentBounds.setHeight(Spring.constant(735));//610
        contentBounds.setX(Spring.constant(10));
        contentBounds.setY(Spring.constant(10));        

        sidePanel(205,735,795,10,players);
        
        int hset = 0;
        ImageIcon image = null;
        String obrazek = "";
        String barva = "";
        SpringLayout.Constraints imageBounds = null;
        JLabel i = null;
        int p = 1;
        int poc = 0;
        for(Field v : pole) {
            if(v.type == 2) {  // normal field
                if(p == 5) {   // koncove pole
                    if(hset == 0) { // nebylo zadano dalsi bude 6
                        v.imageId = 0;
                        obrazek = x[0];
                        hset++;
                    } else {
                        v.imageId = p;
                        obrazek = x[p];
                        hset--;// reset flagu
                        p = 1; // bylo zadano dalsi bude 1
                    }
                } else {
                    v.imageId = p;
                    obrazek = x[p];
                    p++;
                }
                image = new ImageIcon("images/"+config.fieldType+ "/" + obrazek+".png");

            } else if(v.type == 4 || v.type == 3 || v.type == 1) {  // finish home nebo starting field
                if(v.type == 3) {
                    v.imageId = 0;
                    obrazek = x[0];
                    hset++;
                } else {
                    v.imageId = v.id%6;
                    obrazek = x[v.id%6];
                }
                image = new ImageIcon("images/"+config.fieldType+ "/"+obrazek+".png");

            } else { // start home
                image = new ImageIcon("images/"+config.fieldType+ "/"+obrazek+".png");
            }

            if(v.figureLabel != null) { // vykresleni figurek
                Icon a = v.figureLabel.getIcon();
                ImageIcon img = new ImageIcon(a.toString());
                imageBounds = rozmisteni.getConstraints(i = v.figureLabel);
                imageBounds.setX(Spring.constant(v.x));
                imageBounds.setY(Spring.constant(v.y));
                i.setName(Integer.toString(v.id));
                i.addMouseListener(this);
                content.add(i);
            }

            if(v.type != 1) {
                imageBounds = rozmisteni.getConstraints(i = new JLabel(image));
                imageBounds.setX(Spring.constant(v.x));
                imageBounds.setY(Spring.constant(v.y));
                content.add(i);
            }

            if(v.type == 4 || v.type == 3 || v.type == 1) {
                if(v.playerColor > 0)
                    barva = colors[v.playerColor];
                else
                    barva = "s_blue";
                image = new ImageIcon("images/home_"+barva+".png");
                imageBounds = rozmisteni.getConstraints(i = new JLabel(image));
                imageBounds.setX(Spring.constant(v.x));
                imageBounds.setY(Spring.constant(v.y));
                content.add(i);
            }

            //System.out.println("Policko - ID: " + v.id + ", Figurka:" + (v.figure != null ? v.figure.playerId : null) + ", Type: " + v.type + ", ImageId: " + v.imageId + ", Obrazek: " + obrazek + ", PlayerColor:" + v.playerColor + ", Barva:" + barva);
            //System.out.println("Policko - ID: " + v.id + ", Figurka:" + (v.figure != null ? v.figureLabel : null) + ", Type: " + v.type + ", ImageId: " + v.imageId + ", Obrazek: " + obrazek + ", PlayerColor:" + v.playerColor + ", Barva:" + barva);
            //pole.set(poc, v);
            poc++;
        }

        frame.getContentPane().add(content);
        content.revalidate();
        frame.repaint();
    }

    /**
     * ulozi hru do xml
     */
    public void saveAsXml() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("examples"));
        int returnVal = fc.showSaveDialog(frame);
        FileOutputStream fos = null;
        File f = null;

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                //f = new File("soubor.xml");
                f = fc.getSelectedFile();
                fos = new FileOutputStream(f);
            } catch(java.lang.Exception e) {
                System.out.println("chyba otvirani " + e); // TODO: CHYBA pri otvirani souboru
            }

            String str = makeXml();

            try {
                fos.write(String.valueOf(str).getBytes());
            } catch(java.io.IOException e) {
                System.out.println("chyba zapisu " + e);  // TODO:
            }

            try {
                fos.close();
            } catch(java.lang.Exception e) {
                System.out.println("chyba zavirani " + e); // TODO:
            }
        }
    }

    /**
     * vrati string pro ulozeni xml do souboru
     * @return string xml
     */
    public String makeXml() {
        String str = "<?xml version='1.0' encoding='utf-8' standalone='no' ?>\n";
        String aicka = "";
        String images = "";
        for(int a : ai)
            aicka += a + ",";
        for(String y : x)
            images += y + ",";
        aicka = aicka.substring(0, aicka.length()-1);
        images = images.substring(0, images.length()-1);
        //System.out.println(aicka);
        str = str + "<savegame turnDone='"+(turnDone ? 1 : 0)+"' ai='"+aicka+"' cubeValue='"+this.value+"' x='"+images+"' numoffields='"+this.fields+"' numofplayers='"+this.players+"' round='"+this.round+"' human='"+this.human+"'>\n";
        int i = 0;
        for(Field v : pole) {
            str = str + "\t<field>\n";
            str = str + "\t\t<id>" + v.id + "</id>\n";
            str = str + "\t\t<x>" + v.x + "</x>\n";
            str = str + "\t\t<y>" + v.y + "</y>\n";
            str = str + "\t\t<type>" + v.type + "</type>\n";
            str = str + "\t\t<figure>\n";
            if(v.figure != null) {
                str = str + "\t\t\t<playerId>" + v.figure.playerId + "</playerId>\n";
                str = str + "\t\t\t<figId>" + v.figure.id + "</figId>\n";
                str = str + "\t\t\t<position>" + v.figure.position + "</position>\n";
                str = str + "\t\t\t<start>" + v.figure.start + "</start>\n";
                str = str + "\t\t\t<color>" + v.figure.color + "</color>\n";
                str = str + "\t\t\t<startFieldId>" + v.figure.startFieldId + "</startFieldId>\n";
            } else {
                str = str + "\t\t\t<playerId></playerId>\n";
                str = str + "\t\t\t<figId></figId>\n";
                str = str + "\t\t\t<position></position>\n";
                str = str + "\t\t\t<start></start>\n";
                str = str + "\t\t\t<color></color>\n";
                str = str + "\t\t\t<startFieldId></startFieldId>\n";
            }
            str = str + "\t\t</figure>\n";
            str = str + "\t\t<figureLabel>" + (v.figureLabel != null ? v.playerColor : "-1") + "</figureLabel>\n";
            str = str + "\t\t<imageid>" + v.imageId + "</imageid>\n";
            str = str + "\t\t<playerColor>" + v.playerColor + "</playerColor>\n";
            str = str + "\t</field>\n";
            //System.out.println("Policko - ID: " + v.id + ", Figurka:" + (v.figure != null ? v.figure.playerId : null) + ", Type: " + v.type + ", ImageId: " + v.imageId + ", PlayerColor:" + v.playerColor);
            i++;
        }
        str = str + "</savegame>\n";
        return str;
    }

    /**
     * nacte hru z xml a zobrazi hraci pole
     */
    public void loadFromXml(String str) {
        int returnVal;
        File f = null;
        if(str.equals("")) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File("examples"));
            returnVal = fc.showOpenDialog(frame);
            try {
                f = fc.getSelectedFile();
            } catch(java.lang.Exception e) {
                System.out.println("chyba otvirani " + e); // TODO: CHYBA pri otvirani souboru
            }
        } else {
            returnVal = JFileChooser.APPROVE_OPTION;
            FileOutputStream fos = null;
            try {
                f = new File("my_tempfile.xml");
                fos = new FileOutputStream(f);
            } catch(java.lang.Exception e) {
                System.out.println("chyba otvirani " + e); // TODO: CHYBA pri otvirani souboru
            }

            try {
                fos.write(String.valueOf(str).getBytes());
            } catch(java.io.IOException e) {
                System.out.println("chyba zapisu " + e);  // TODO:
            }

            try {
                fos.close();
            } catch(java.lang.Exception e) {
                System.out.println("chyba zavirani " + e); // TODO:
            }
        }

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            Document document = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                 builder = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            try {
                document = builder.parse(f);
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            NodeList load = document.getElementsByTagName("savegame");
            String aicka = load.item(0).getAttributes().getNamedItem("ai").getNodeValue();
            String ia[] = aicka.split(",");
            int i = 0;
            for(String iaa : ia) {
                //System.out.println("a"+iaa);
                ai[i] = Integer.valueOf(iaa);
                i++;
            }

            String images = load.item(0).getAttributes().getNamedItem("x").getNodeValue();
            String imgs[] = images.split(",");
            i = 0;
            for(String iaa : imgs) {
                //System.out.println("a"+iaa);
                x[i] = iaa;
                i++;
            }

            //System.out.println(ai[0]+" " +ai[1]+ai[2]+ai[3]+ai[4]+ai[5]+ai[6]);
            human = Integer.valueOf(load.item(0).getAttributes().getNamedItem("human").getNodeValue());
            int turn = Integer.valueOf(load.item(0).getAttributes().getNamedItem("turnDone").getNodeValue());
            if(turn == 1)
              turnDone = true;
            else
              turnDone = false;

            value = Integer.valueOf(load.item(0).getAttributes().getNamedItem("cubeValue").getNodeValue());
            //System.out.println(value);
            ImageIcon image;
            if(value == -1)
              image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[0]+"_cube.jpg");
            else
              image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[value]+"_cube.jpg");
            loskostkos.setIcon(image);
            round = Integer.valueOf(load.item(0).getAttributes().getNamedItem("round").getNodeValue());
            fields = Integer.valueOf(load.item(0).getAttributes().getNamedItem("numoffields").getNodeValue());
            players = Integer.valueOf(load.item(0).getAttributes().getNamedItem("numofplayers").getNodeValue());

            load = document.getElementsByTagName("field");
            pole.clear();
            Field fl = null;
            int id, xko, y, type, imageId, playerColor, figPlayerId, figId, figPosition, figStart, figStartField, figColor = 0;
            Figure figure = null;
            String barva;
            image = null;
            SpringLayout.Constraints imageBounds = null;
            JLabel label = null;
            Node node = null;
            for (i = 0; i < load.getLength(); i++) {
                NodeList nl = load.item(i).getChildNodes();
                // 1 id
                // 3 x
                // 5 y
                // 7 type
                // 9 figure
                    // 1 playerId
                    // 3 figId
                    // 5 position
                    // 7 start
                    // 9 color
                    // 11 startField
                // 11 figureLabel
                // 13 imageId
                // 15 playerColor
                id = Integer.valueOf(nl.item(1).getChildNodes().item(0).getNodeValue());
                xko = Integer.valueOf(nl.item(3).getChildNodes().item(0).getNodeValue());
                y = Integer.valueOf(nl.item(5).getChildNodes().item(0).getNodeValue());
                type = Integer.valueOf(nl.item(7).getChildNodes().item(0).getNodeValue());

                if((node = nl.item(9).getChildNodes().item(1).getChildNodes().item(0)) != null)
                    figPlayerId = Integer.valueOf(node.getNodeValue()); //figure -> playerId
                else
                    figPlayerId = -99;

                if((node = nl.item(9).getChildNodes().item(3).getChildNodes().item(0)) != null)
                    figId = Integer.valueOf(node.getNodeValue()); //figure -> figId
                else
                    figId = -99;

                if((node = nl.item(9).getChildNodes().item(5).getChildNodes().item(0)) != null)
                    figPosition = Integer.valueOf(node.getNodeValue()); //figure -> position
                else
                    figPosition = -99;

                if((node = nl.item(9).getChildNodes().item(7).getChildNodes().item(0)) != null)
                    figStart = Integer.valueOf(node.getNodeValue()); //figure -> start
                else
                    figStart = -99;

                if((node = nl.item(9).getChildNodes().item(9).getChildNodes().item(0)) != null)
                    figColor = Integer.valueOf(node.getNodeValue()); //figure -> color
                else
                    figColor = -99;

                if((node = nl.item(9).getChildNodes().item(11).getChildNodes().item(0)) != null)
                    figStartField = Integer.valueOf(node.getNodeValue()); //figure -> startField
                else
                    figStartField = -99;

                imageId = Integer.valueOf(nl.item(13).getChildNodes().item(0).getNodeValue());
                playerColor = Integer.valueOf(nl.item(15).getChildNodes().item(0).getNodeValue());
                if(figPlayerId != -99 && figPosition != -99 && figStart != -99 && figColor !=-99 && figStartField != -99) {
                    figure = new Figure(figPlayerId, figId, figStartField, figStart, figColor);
                    figure.setPosition(figPosition);
                }
                else
                    figure = null;

                if(figure != null) {
                    barva = colors[figColor];
                    image = new ImageIcon("images/"+barva+".png");
                } else {
                    image = new ImageIcon("images/blank.png");
                }
                imageBounds = rozmisteni.getConstraints(label = new JLabel(image));
                imageBounds.setX(Spring.constant(xko));
                imageBounds.setY(Spring.constant(y));

                fl = new Field(id, xko, y, type, imageId, figure, label, playerColor);
                pole.add(fl);
            }
            drawGamePanel();
            this.info.setText("Na tahu: " + hrac[round%players] + " hráč ");
        }
    }

    /**
     * pomocna funkce pro generovani rhaciho pole, generuje pouze jeho cast
     * @param id - id casti
     * @param rowFields - pocet policek kazde casti
     * @param x - pole obsahujici zamichane obrazky
     */
    public void generateRow(int id, int rowFields, String[] x) {
        int posunX = 0;
        switch(this.fields) {
            case 72: posunX = 70;break;
            case 60: posunX = 122;break;
            case 48: posunX = 175;break;
            case 36: posunX = 227;break;
        }
        int posunY = 35;
        int smer = 2;
        int color = 1;

        switch(id) {
            case 1: color = 0;smer = 1;break; // vodorovne LR
            case 3: smer = 1;break; // vodorovne LR
            case 11: color = 7;smer = 1;break; // vodorovne LR
            case 2: color = 1;smer = 2;break;  // svisle TB
            case 4: color = 2;smer = 2;break;  // svisle TB
            case 6: smer = 2;break;  // svisle TB
            case 5: color = 3;smer = 3;break;  // vodorovne RL
            case 7: color = 4;smer = 3;break;  // vodorovne RL
            case 9: smer = 3;break;  // vodorovne RL
            case 8: color = 5;smer = 4;break; // svisle BT
            case 10: color = 6;smer = 4;break; // svisle BT
            case 12: smer = 4;break; // svisle BT
        }


        if(this.fields == 36)
            switch(id) {
                case 1: color = 0;break;
                case 4: color = 1;break;
                case 7: color = 2;break;
                case 10: color = 3;break;
            }

        int startX = 0;
        int startY = 0;
        switch(id) {
            case 1: startX = (rowFields*fieldWidth+posunX); startY = posunY; break;
            case 2: startX = ((rowFields*2)*fieldWidth+posunX); startY = posunY; break;
            case 3: startX = ((rowFields*2)*fieldWidth+posunX); startY = (rowFields*fieldWidth+posunY); break;
            case 4: startX = ((rowFields*3)*fieldWidth+posunX); startY = (rowFields*fieldWidth+posunY); break;
            case 5: startX = ((rowFields*3)*fieldWidth+posunX); startY = ((rowFields*2)*fieldWidth+posunY); break;
            case 6: startX = ((rowFields*2)*fieldWidth+posunX); startY = ((rowFields*2)*fieldWidth+posunY); break;
            case 7: startX = ((rowFields*2)*fieldWidth+posunX); startY = ((rowFields*3)*fieldWidth+posunY); break;
            case 8: startX = (rowFields*fieldWidth+posunX); startY = ((rowFields*3)*fieldWidth+posunY); break;
            case 9: startX = (rowFields*fieldWidth+posunX); startY = ((rowFields*2)*fieldWidth+posunY); break;
            case 10: startX = posunX; startY = ((rowFields*2)*fieldWidth+posunY); break;
            case 11: startX = posunX; startY = (rowFields*fieldWidth+posunY); break;
            case 12: startX = (rowFields*fieldWidth+posunX); startY = (rowFields*fieldWidth+posunY); break;
        }

        int setx;
        int sety;
        Field policko;
        for(int j=0;j < rowFields;j++) {
            if(smer == 1) {         // vodorovne LR
                setx = j*fieldWidth + startX;
                sety = startY;
            } else if(smer == 2) {  // svisle TB
                setx = startX;
                sety = j*fieldWidth + startY;
            } else if(smer == 3) {  // vodoorovne RL
                setx = startX - j*fieldWidth;
                sety = startY;
            } else if(smer == 4) {  // svisle BT
                setx = startX;
                sety = startY - j*fieldWidth;
            } else {
                setx = 0;
                sety = 0;
            }

            String barva = "";
            ImageIcon image = null;
            SpringLayout.Constraints imageBounds = null;
            JLabel i = null;
            JLabel label = null;
            if(id % 3 != 0 && j == 0) {
                barva = colors[color];
                image = new ImageIcon("images/"+barva+".png");
                imageBounds = rozmisteni.getConstraints(i = new JLabel(image));
                imageBounds.setX(Spring.constant(setx));
                imageBounds.setY(Spring.constant(sety));

                image = new ImageIcon("images/blank.png");
                imageBounds = rozmisteni.getConstraints(label = new JLabel(image));
                imageBounds.setX(Spring.constant(setx));
                imageBounds.setY(Spring.constant(sety));

                if(this.fields > 36)
                    policko = new Field(this.poradi, setx, sety, 3, this.poradi%6, null, label, color); // starting field
                else if(id % 3 == 1)
                    policko = new Field(this.poradi, setx, sety, 3, this.poradi%6, null, label, color); // starting field
                else
                    policko = new Field(this.poradi, setx, sety, 2, this.poradi%6, null, label, -1); // starting field
            } else {
                image = new ImageIcon("images/blank.png");
                imageBounds = rozmisteni.getConstraints(label = new JLabel(image));
                imageBounds.setX(Spring.constant(setx));
                imageBounds.setY(Spring.constant(sety));

                policko = new Field(this.poradi, setx, sety, 2, this.poradi%6, null, label, -1); // normal field
            }
            this.poradi++;
            pole.add(policko);
        }
    }

    /**
     * vytiskne chybovou hlasku
     * @param str hlaska k vytisknuti
     */
    public void printError(String str) {
        JOptionPane.showMessageDialog(this, str, "Chyba", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * generuje startovni domecky ke kazde casti pole
     * @param id - id casti pole
     */
    public void generateStartHomes(int id) {
        Field policko;
        int startX = 0;
        int startY = 0;
        int setx = 0;
        int sety = 0;
        int i;
        switch(fields) {
            case 72:
                switch(id) {
                    case 1:
                        startX = 175;
                        startY = 35;
                        break;
                    case 2:
                        startX = 560;
                        startY = 35;
                        break;
                    case 4:
                        startX = 665;
                        startY = 140;
                        break;
                    case 5:
                        startX = 665;
                        startY = 525;
                        break;
                    case 7:
                        startX = 560;
                        startY = 630;
                        break;
                    case 8:
                        startX = 175;
                        startY = 630;
                        break;
                    case 10:
                        startX = 70;
                        startY = 525;
                        break;
                    case 11:
                        startX = 70;
                        startY = 140;
                        break;
                    default:
                        startX = 0;
                        startY = 0;
                        break;
                }
                break;
             case 60:
                switch(id) {
                    case 1:
                        startX = 175;
                        startY = 35;
                        break;
                    case 2:
                        startX = 525;
                        startY = 35;
                        break;
                    case 4:
                        startX = 595;
                        startY = 105;
                        break;
                    case 5:
                        startX = 595;
                        startY = 455;
                        break;
                    case 7:
                        startX = 525;
                        startY = 525;
                        break;
                    case 8:
                        startX = 175;
                        startY = 525;
                        break;
                    case 10:
                        startX = 105;
                        startY = 455;
                        break;
                    case 11:
                        startX = 105;
                        startY = 105;
                        break;
                    default:
                        startX = 0;
                        startY = 0;
                        break;
                }
                startX += 17;
                break;
            case 48:
                switch(id) {
                    case 1:
                        startX = 210;
                        startY = 35;
                        break;
                    case 2:
                        startX = 455;
                        startY = 35;
                        break;
                    case 4:
                        startX = 525;
                        startY = 105;
                        break;
                    case 5:
                        startX = 525;
                        startY = 350;
                        break;
                    case 7:
                        startX = 455;
                        startY = 420;
                        break;
                    case 8:
                        startX = 210;
                        startY = 420;
                        break;
                    case 10:
                        startX = 140;
                        startY = 350;
                        break;
                    case 11:
                        startX = 140;
                        startY = 105;
                        break;
                    default:
                        startX = 0;
                        startY = 0;
                        break;
                }
                startX += 35; // posun domecku
                break;
            case 36:
                switch(id) {
                    case 1:
                        startX = 175;
                        startY = 35;
                        break;
                    case 4:
                        startX = 455;
                        startY = 35;
                        break;
                    case 7:
                        startX = 455;
                        startY = 315;
                        break;
                    case 10:
                        startX = 175;
                        startY = 315;
                        break;
                    default:
                        startX = 0;
                        startY = 0;
                        break;
                }
                startX += 52; // posun domecku
                break;
        }
        int color = 1;
        if(this.fields == 36)
            switch(id) {
                case 1: color = 0;break;
                case 4: color = 1;break;
                case 7: color = 2;break;
                case 10: color = 3;break;
            }
        else
            switch(id) {
                case 1: color = 0;break;
                case 2: color = 1;break;
                case 4: color = 2;break;
                case 5: color = 3;break;
                case 7: color = 4;break;
                case 8: color = 5;break;
                case 10: color = 6;break;
                case 11: color = 7;break;
            }
        Figure figurka = null;
        String barva = "";
        ImageIcon image = null;
        SpringLayout.Constraints imageBounds = null;
        JLabel label = null;
        int play = 0;
        if(this.fields > 36)
            play = color;
        else {
            switch(id) {
                case 1: play = 0;break;
                case 4: play = 1;break;
                case 7: play = 2;break;
                case 10: play = 3;break;
            }
        }

        int startF = -1;
        if(players > color || (this.fields == 36 && players == 2 && play == 1) || (this.fields == 36 && players == 3 && play == 2) || (this.fields == 36 && players == 4 && play == 3)) {
            for(Field v : pole) {
                if(v.type == 3 && v.playerColor == color) {
                    startF = v.id;
                    break;
                }
            }
        } else {
            startF = -1;
        }

        for(i=0; i<2; i++) {
            setx = (i * fieldWidth) + startX;
            sety = startY;
            if(players > color || (this.fields == 36 && players == 2 && play == 1) || (this.fields == 36 && players == 3 && play == 2) || (this.fields == 36 && players == 4 && play == 3)) {
                figurka = new Figure(play, id+i, startF, this.poradi, color);
                figurka.setPosition(this.poradi);
                barva = colors[color];
                image = new ImageIcon("images/"+barva+".png");
                imageBounds = rozmisteni.getConstraints(label = new JLabel(image));
                imageBounds.setX(Spring.constant(setx));
                imageBounds.setY(Spring.constant(sety));
            } else {
                image = new ImageIcon("images/blank.png");
                imageBounds = rozmisteni.getConstraints(label = new JLabel(image));
                imageBounds.setX(Spring.constant(setx));
                imageBounds.setY(Spring.constant(sety));
                figurka = null;
            }
            policko = new Field(this.poradi, setx, sety, 1, -1, figurka, label, color);  // 1. a 2. policko
            this.poradi++;
            pole.add(policko);
        }
        for(i=0; i<2; i++) {
            setx = (i * fieldWidth) + startX;
            sety = startY + fieldWidth;
            if(players > color || (this.fields == 36 && players == 2 && play == 1) || (this.fields == 36 && players == 3 && play == 2) || (this.fields == 36 && players == 4 && play == 3)) {
                figurka = new Figure(play, id+i, startF, this.poradi, color);
                figurka.setPosition(this.poradi);
                barva = colors[color];
                image = new ImageIcon("images/"+barva+".png");
                imageBounds = rozmisteni.getConstraints(label = new JLabel(image));
                imageBounds.setX(Spring.constant(setx));
                imageBounds.setY(Spring.constant(sety));
            } else {
                image = new ImageIcon("images/blank.png");
                imageBounds = rozmisteni.getConstraints(label = new JLabel(image));
                imageBounds.setX(Spring.constant(setx));
                imageBounds.setY(Spring.constant(sety));
                figurka = null;
            }
            policko = new Field(this.poradi, setx, sety, 1, -1, figurka, label, color);  // 3. a 4. policko
            this.poradi++;
            pole.add(policko);
        }
    }

    /**
     * generuje koncove domecky
     * @param startX - x-ova pozice prvniho
     * @param startY - y-ova pozice prvniho
     * @param id - id casti
     */
    public void generateFinishHomes(int startX, int startY, int id) {
        int smer = 1;
        switch(id) {
            case 1:
                startX += 35;
                startY += 35;
                smer = 1; // 1 svisle dolu, 2- doleva, 3 nahoru, 4 doprava
                break;
            case 2:
                startX -= 35;
                startY += 35;
                smer = 1; // 1 svisle dolu, 2- doleva, 3 nahoru, 4 doprava
                break;
            case 4:
                startX -= 35;
                startY += 35;
                smer = 2; // 1 svisle dolu, 2- doleva, 3 nahoru, 4 doprava
                break;
            case 5:
                startX -= 35;
                startY -= 35;
                smer = 2; // 1 svisle dolu, 2- doleva, 3 nahoru, 4 doprava
                break;
            case 7:
                startX -= 35;
                startY -= 35;
                smer = 3; // 1 svisle dolu, 2- doleva, 3 nahoru, 4 doprava
                break;
            case 8:
                startX += 35;
                startY -= 35;
                smer = 3; // 1 svisle dolu, 2- doleva, 3 nahoru, 4 doprava
                break;
            case 10:
                startX += 35;
                startY -= 35;
                smer = 4; // 1 svisle dolu, 2- doleva, 3 nahoru, 4 doprava
                break;
            case 11:
                startX += 35;
                startY += 35;
                smer = 4; // 1 svisle dolu, 2- doleva, 3 nahoru, 4 doprava
                break;
        }
        int sety;
        int setx;
        Field policko;
        String barva = "";
        ImageIcon image = null;
        SpringLayout.Constraints imageBounds = null;
        JLabel label = null;
        for(int j = 0; j < 4; j++) {
            if(smer == 1) {
                setx = startX;
                sety = j*fieldWidth + startY;
            } else if(smer == 2) {
                setx = startX - j*fieldWidth;
                sety = startY;
            } else if(smer == 3) {
                setx = startX;
                sety = startY - j*fieldWidth;
            } else if(smer == 4) {
                setx = j*fieldWidth + startX;
                sety = startY;
            } else {
                setx = 0;
                sety = 0;
            }
            int color = 1;
            if(this.fields == 36)
            switch(id) {
                case 1: color = 0;break;
                case 4: color = 1;break;
                case 7: color = 2;break;
                case 10: color = 3;break;
            }
        else
            switch(id) {
                case 1: color = 0;break;
                case 2: color = 1;break;
                case 4: color = 2;break;
                case 5: color = 3;break;
                case 7: color = 4;break;
                case 8: color = 5;break;
                case 10: color = 6;break;
                case 11: color = 7;break;
            }

            image = new ImageIcon("images/blank.png");
            imageBounds = rozmisteni.getConstraints(label = new JLabel(image));
            imageBounds.setX(Spring.constant(setx));
            imageBounds.setY(Spring.constant(sety));

            policko = new Field(this.poradi, setx, sety, 4, this.poradi%6, null, label, color);  // finish homes
            this.poradi++;
            pole.add(policko);
        }
    }

    /**
     * vrati pole s policky
     * @return ArrayList pole s policky
     */
    public ArrayList<Field> getArr() {
        return this.pole;
    }

    /**
     * vrati pocet policek
     * @return id pocet policek
     */
    public int getFields() {
        return this.fields;
    }

    /**
     * zobrazi napovedu
     * @throws IOException
     */
    public void showHelp() throws IOException {
        JHelp helpViewer = null;
        try {
                URL hsURL = HelpSet.findHelpSet (null, "HelpSet.hs");
                ClassLoader cl = Frame.class.getClassLoader();
                helpViewer = new JHelp(new HelpSet (cl, hsURL));
        } catch (Exception ee) {
                System.err.println("Helpset nebyl nalezen!");
                return;
        }

        JFrame fr = new JFrame();
        fr.getContentPane().add(helpViewer);
        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension win = fr.getToolkit().getScreenSize();
        fr.setBounds(win.width / 2 - 512, win.height / 2 - 350, 500, 500);
        fr.setVisible(true);
    }

    /**
     * zobrazi okno pro zmenu nastaveni
     */
    public void showOptions() {
        opt = new JFrame("Nastaveni");

        Toolkit obraz = opt.getToolkit();
        Dimension size = obraz.getScreenSize(); // nacte velikost okna
        opt.setBounds(size.width/2-300, size.height/2-300, 300, 300); // otevre okno presne uprostred obrazovky
        opt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        opt.setLayout(new FlowLayout());

        SpringLayout layout = new SpringLayout();

        JPanel tit = new JPanel(new FlowLayout());
        String[] types = {"typ1","typ2","typ3"};
        select = new JComboBox(types);
        int i = 0;
        for(String a:types) {
            if(a.equals(config.fieldType))
                select.setSelectedIndex(i);
            i++;
        }
        tit.add(new JLabel("Typ obrázků:"));
        tit.add(select);
        opt.getContentPane().add(tit);
        JPanel tit1 = new JPanel(new FlowLayout());
        tit1.add(new JLabel("Jméno hráče:"));
        name = new JTextField(config.playerName, 10);
        tit1.add(name);
        opt.getContentPane().add(tit1);

        JPanel tit2 = new JPanel(new FlowLayout());
        JButton ok = new JButton("OK");
        ok.addActionListener(this);
        JButton cancel = new JButton("CANCEL");
        cancel.addActionListener(this);
        tit2.add(ok);
        tit2.add(cancel);
        opt.getContentPane().add(tit2);
        opt.setVisible(true);
    }

    /**
     * obsluhuje akce
     * @param e objekt na ktery bylo kliknuto
     */
    public void actionPerformed(ActionEvent e) {
        if("CANCEL".equals(e.getActionCommand()))
        {
            opt.dispose();
        }
        else if("OK".equals(e.getActionCommand()))
        {
            config.saveConfig(this);
            config.loadConfig();
            drawGamePanel();
            JOptionPane.showMessageDialog(frame, "Nastavení bylo uloženo, pro efekt musíte restartovat aplikaci.", "Info", 1);
            opt.dispose();
        }
        else if("Obsah napovedy".equals(e.getActionCommand())) {
            try {
                showHelp();
            } catch(java.io.IOException a) {
                System.out.println("Chyba napovedy");
            }
        }
        else if("Nastaveni".equals(e.getActionCommand())) {
            showOptions();
        }
        else if("Ulozit jako".equals(e.getActionCommand())) {
            if(netgame == 1)
            {
               Packet p = new Packet(3, gamename, players, fields, makeXml(), "", round%players);
                try {
                    out.reset();
                    out.writeObject(p);
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                saveAsXml();
            }
        }
        else if("Nacist hru".equals(e.getActionCommand())) {
            loadFromXml("");
        }
        else if("O Aplikaci".equals(e.getActionCommand())) {  // zobrazi kratke info
            String info = "Hra Člověče, nezlob se. \nVytvořeno jako projekt pro předmět IJA. \nAutoři: \n   Jakub Nowak (xnowak03) \n   Igor Pavlů (xpavlu06)";
            JOptionPane.showMessageDialog(frame, info, "O Aplikaci", 1);
        }
        else if("Konec".equals(e.getActionCommand())) { // ukonceni programu
          konec();
        }
        else if ("Nova hra".equals(e.getActionCommand()))
        {
            konec2();
        }
    }

    /**
     * zobrazi potvrzeni o ukonceni aplikace
     */
    private void konec() {
      Object[] buttons = {"Ano", "Ne"};
        int n = JOptionPane.showOptionDialog(frame, "Opravdu si přejete ukončit hru?", "Konec", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        if(n==0) {
            if(netgame == 1){
            try {
                out.reset();
                Packet p = new Packet(5, gamename, players, fields, "", "", netplayernum);
                out.writeObject(p);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }}
            frame.dispose();
        }
    }

    /**
     * zobrazi potvrzeni o ukonceni hry
     */
    private void konec2() {
        int n = 0;
        if(this.players > 0 && this.fields > 0) {
            Object[] buttons = {"Ano", "Ne"};
            n = JOptionPane.showOptionDialog(frame, "Přejete si ukončit hru?", "Konec", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        }
        if(n==0) {
            if(netgame == 1){
            try {
                out.reset();
                Packet p = new Packet(5, gamename, players, fields, "", "", netplayernum);
                out.writeObject(p);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }}
            frame.dispose();
            new StartFrame();
        } else {
            // nedelame nic
        }
    }

    public void runPc() {
        Cube c = new Cube();
        c.setValue();
        int v = c.getValue();

        MoveCalc mc = new MoveCalc(round%players,ai[round%players], v, pole,fields);
        ImageIcon image = new ImageIcon("images/"+config.fieldType+"/cube/ff6_cube.jpg");
        switch(v)
        {
            case 0:image = new ImageIcon("images/"+config.fieldType+"/"+s[v]+".jpg");break;
            case 1:image = new ImageIcon("images/"+config.fieldType+"/"+s[v]+".jpg");break;
            case 2:image = new ImageIcon("images/"+config.fieldType+"/"+s[v]+".jpg");break;
            case 3:image = new ImageIcon("images/"+config.fieldType+"/"+s[v]+".jpg");break;
            case 4:image = new ImageIcon("images/"+config.fieldType+"/"+s[v]+".jpg");break;
            case 5:image = new ImageIcon("images/"+config.fieldType+"/"+s[v]+".jpg");break;
        }
        loskostkos.setIcon(image);

        int tmp = mc.calculate(round%players, ai[round%players], v, pole);
        if(v != 0)
        {
            round++;

            if(round%players >= human)
            {
                info.setText("Na tahu: " + hrac[round%players] + " hráč " + log[ai[round%players]]);
                runPc();
            }
            else
            {
                info.setText("Na tahu: " + hrac[round%players] + " hráč ");
            }
        }
        else
        {
            runPc();
        }
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getSource() instanceof JLabel)
        {
            JLabel l = (JLabel) e.getSource();
            if(l.getName().equals("kostka") && turnDone == true && (netgame == 0 || (netgame == 1 && send && !lock)))
            {
                if((netgame == 1 && netplayernum == round%players && !lock) || netgame != 1 ) // jeslize je sitova hra a zaroven jsem na tahu tak muzu hazet
                {
                    if(netgame == 1) lock = true;
                    turnDone = false;
                    send = false;
                    Cube c = new Cube();
                    c.setValue();
                    this.value = c.getValue();
                    ImageIcon image = new ImageIcon("images/"+config.fieldType+"/cube/ff6_cube.jpg");

                    switch(value)
                    {
                        case 0:image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[value]+"_cube.jpg");break;
                        case 1:image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[value]+"_cube.jpg");break;
                        case 2:image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[value]+"_cube.jpg");break;
                        case 3:image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[value]+"_cube.jpg");break;
                        case 4:image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[value]+"_cube.jpg");break;
                        case 5:image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[value]+"_cube.jpg");break;
                    }
                    if(value == -1)
                    {
                        image = new ImageIcon("images/"+config.fieldType+"/cube/"+x[0]+"_cube.jpg");
                    }
                    loskostkos.setIcon(image);
                    send = false;
                    MoveCalc mc = new MoveCalc(round%players,typeAI, value, pole,fields);
                    if(mc.moveEnable(pole, round%players, value) == 0)
                    {
                        round++;   
                        turnDone = true;
                        info.setText("Na tahu: " + hrac[round%players] + " hráč ");
                        if(netgame == 1)
                        {
                             send = true;
                            sendXml(makeXml());
                        }
                            
                        
                        if(round%players >= human && netgame == 0)
                        {
                            info.setText("Na tahu: " + hrac[round%players] + " hráč " + log[ai[round%players]]);
                            runPc();
                        }
                        else
                        {
                            info.setText("Na tahu: " + hrac[round%players] + " hráč ");
                        }
                    }
                    else{

                    }
                }
            }
            else if(l.getName().equals("kostka") && (turnDone == false || (netgame == 1 && round%players != netplayernum)) )
            {}
            else
            {
               String str = l.getName();
               int v = Integer.parseInt(str);
               //System.out.println(v);
               MoveCalc mc = new MoveCalc(round%players,ai[round%players], value, pole,fields);
               Field f = pole.get(v);
               if(!turnDone && f.figure != null && f.figure.playerId == round%players)
               {
                   if(mc.playerMove(round%players, v, fields, value, pole))
                   {
                       turnDone = true;
                       if(value != 0)
                       {   
                           round++;
                           this.info.setText("Na tahu: " + hrac[round%players] + " hráč ");
                       }
                       send = false;
                        lock = true;  
                       if(netgame == 1) 
                       {
                            send = true;
                           sendXml(makeXml());
                       }
                       
                            if(round%players >= human && netgame == 0)
                           {
                               turnDone = true;
                               runPc();
                           }
                         
                             
                            }             
                        
               }
            }
        }
        //if(netgame == 1 && send == false)
       // {
        //    sendXml(makeXml());
        //    send = true;
        //    loadFromXml(makeXml());
        
        if(netgame == 1 && lock)
        {
          
            lock = false;
           
           readXml();
            //ReadNet r = new ReadNet(this);
            //r.readIt();
            //if(round % players == netplayernum) lock = false;
        }
        
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    /**
     * zobrazi postranni panel
     * @param width sirka
     * @param height vyska
     * @param x - x-ova pocatecni souradnice
     * @param y - y-ova pocatecni souradnice
     * @param playerNum - pocet hracu
     */
    private void sidePanel(int width, int height, int x, int y, int playerNum) {
        position = new SpringLayout();
        infoPanel = new JPanel(position);

        infoPanelBounds = rozmisteni.getConstraints(infoPanel);
        infoPanelBounds.setWidth(Spring.constant(width-5));//205
        infoPanelBounds.setHeight(Spring.constant(height-5));//735
        infoPanelBounds.setX(Spring.constant(x));//795
        infoPanelBounds.setY(Spring.constant(y));//10
        frame.add(infoPanel);

        showInfoBlock2(0,0, width, height);
        if(netgame==0)
        {
            showInfoBlock1(0,95, width,height);
            showInfoBlock3(0,250, width, height);
        }
        else
        {
            showInfoBlock3(0,95, width, height);
        }

    }

    /**
     * prvni cast postranniho panelu
     * @param x - x-ova pocatecni souradnice
     * @param y - y-ova pocatecni souradnice
     * @param width - sirka
     * @param height - vyska
     */
    public void showInfoBlock1(int x, int y, int width, int height) {
        TitledBorder title = BorderFactory.createTitledBorder(blackline, "Aktuální inteligence PC");
        title.setTitleJustification(TitledBorder.CENTER);

        playerPanel = new JPanel(new GridLayout(7,1));
        playerPanelBounds = position.getConstraints(playerPanel);
        playerPanelBounds.setWidth(Spring.constant(width-6));
        playerPanelBounds.setHeight(Spring.constant(150));
        playerPanelBounds.setX(Spring.constant(x));
        playerPanelBounds.setY(Spring.constant(y));
        playerPanel.setBorder(title);
        String[] s = {"PC 1","PC 2","PC 3","PC 4", "PC 5", "PC 6", "PC 7"};
        JComboBox cb = new JComboBox(s);

        cb.setEditable(false);
        cb.addActionListener(new ActionListener()
                {public void actionPerformed(ActionEvent e)
                {
                    JComboBox ns = (JComboBox)e.getSource();
                    String newSelection = (String)ns.getSelectedItem();
                    if("PC 1".equals(newSelection))
                    {
                        PCAI = 0;
                        System.out.println(PCAI);
                    };
                    if("PC 2".equals(newSelection))
                    {
                        PCAI = 1;
                        System.out.println(PCAI);
                    };
                    if("PC 3".equals(newSelection))
                    {
                        PCAI = 2;
                        System.out.println(PCAI);
                    };
                    if("PC 4".equals(newSelection))
                    {
                        PCAI = 3;
                        System.out.println(PCAI);
                    };
                    if("PC 5".equals(newSelection))
                    {
                        PCAI = 4;
                        System.out.println(PCAI);
                    };
                    if("PC 6".equals(newSelection))
                    {
                        PCAI = 5;
                        System.out.println(PCAI);
                    };
                    if("PC 7".equals(newSelection))
                    {
                        PCAI = 6;
                        System.out.println(PCAI);
                    };

                }});
        playerPanel.add(cb);
        JButton b = new JButton("Ok");
        b.addActionListener(new ActionListener()
                {public void actionPerformed(ActionEvent e)
                {
                    //System.out.println("here");
                    if("Ok".equals(e.getActionCommand()))
                    {
                        if(human + PCAI < 7)
                        {
                          ai[human + PCAI] = typeAI;
                        }
                    };
                }});
        playerPanel.add(b);
        JRadioButton longJump = new JRadioButton("Nejdelší skok", false);
        JRadioButton nasad = new JRadioButton("Nasaď", false);
        JRadioButton vyhod = new JRadioButton("Vyhoď", false);
        JRadioButton domecek = new JRadioButton("Do domečku", false);
        JRadioButton random = new JRadioButton("Jeď jak chceš", true);

        longJump.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
              if(e.getStateChange() == ItemEvent.SELECTED)
                  typeAI = 1;
            }
        });
        nasad.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
              if(e.getStateChange() == ItemEvent.SELECTED)
                  typeAI = 2;
            }
        });
        vyhod.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
              if(e.getStateChange() == ItemEvent.SELECTED)
                  typeAI = 3;
            }
        });
        domecek.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
              if(e.getStateChange() == ItemEvent.SELECTED)
                  typeAI = 4;
            }
        });
        random.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
              if(e.getStateChange() == ItemEvent.SELECTED)
                  typeAI = 0;
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(random); // 0 random
        group.add(longJump); // 1 longJump
        group.add(nasad); // 2 nasad
        group.add(vyhod); // 3 vyhod
        group.add(domecek); // 4 domecek

        playerPanel.add(random);
        playerPanel.add(longJump);
        playerPanel.add(nasad);
        playerPanel.add(vyhod);
        playerPanel.add(domecek);

        infoPanel.add(playerPanel);
        //playerPanel.add(label1);
    }

    /**
     * druha cast postranniho panelu - pc inteligence
     * @param x - x-ova pocatecni souradnice
     * @param y - y-ova pocatecni souradnice
     * @param width - sirka
     * @param height - vyska
     */
    public void showInfoBlock2(int x, int y, int width, int height) {
       // label2 = new JLabel("Informace o hĹ™e");
        TitledBorder title = BorderFactory.createTitledBorder(blackline,"Informace o hře");
        title.setTitleJustification(TitledBorder.CENTER);
        JLabel label4 = new JLabel("Počet hráčů: "+this.players);
        //JLabel label5 = new JLabel("Id hry: %ID%");
        iPanel = new JPanel(new GridLayout(3,1));
        iPanelBounds = position.getConstraints(iPanel);
        info.setText("Na tahu: " + hrac[0] + " hráč");
        iPanelBounds.setWidth(Spring.constant(width-6));
        iPanelBounds.setHeight(Spring.constant(90));
        iPanelBounds.setX(Spring.constant(x));
        iPanelBounds.setY(Spring.constant(y));
       // iPanel.setBackground(Color.BLUE);
        iPanel.add(info);
        iPanel.setBorder(title);
        infoPanel.add(iPanel);
       // iPanel.add(label2);
        iPanel.add(label4);
      //  iPanel.add(label5);
    }

    /**
     * treti cast postranniho panelu - kostka
     * @param x - x-ova pocatecni souradnice
     * @param y - y-ova pocatecni souradnice
     * @param width - sirka
     * @param height - vyska
     */
    public void showInfoBlock3(int x, int y, int width, int height) {
        TitledBorder title = BorderFactory.createTitledBorder(blackline,"Kostka");
        title.setTitleJustification(TitledBorder.CENTER);
        cubePanel = new JPanel();
        cubePanelBounds = position.getConstraints(cubePanel);
        cubePanelBounds.setWidth(Spring.constant(width-6));
        cubePanelBounds.setHeight(Spring.constant(120));
        cubePanelBounds.setX(Spring.constant(x));
        cubePanelBounds.setY(Spring.constant(y));
        //cubePanel.setBackground(Color.GREEN);
        cubePanel.setBorder(title);
        ImageIcon image;
        if(value == -1)
            image = new ImageIcon("images/"+config.fieldType+"/cube/"+ this.x[0]+ "_cube.jpg");
        else image = new ImageIcon("images/"+config.fieldType+"/cube/"+ this.x[value]+ "_cube.jpg");
       // loskostkos = new JLabel();
        loskostkos.setBorder(blackline);
        loskostkos.setName("kostka");
        loskostkos.setIcon(image);
        loskostkos.addMouseListener(this);
        //ml = new MyMouseListener();
        //ml.setVals(fieldSize,playerNum,typeAI,pole);
        //l.addMouseListener(ml);
        //cubePanel.add(label3,BorderLayout.NORTH);
        cubePanel.add(loskostkos,BorderLayout.SOUTH);
        infoPanel.add(cubePanel);
    }

    public void sendXml(String XML)
    {
        Packet p = new Packet(2, gamename, players, fields, XML, "", round%players);
        try {
            send = true;
            out.reset();
            out.writeObject(p);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void readXml()
    {
       /* try {
            Thread.sleep(5,5);
        } catch (InterruptedException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        while(round % players != netplayernum)
            {
                
                //System.out.println("here" + round);
                try {
                    Packet p2 = (Packet) in.readObject();
                    if(p2 == null) continue;
                    if(p2.op == 0)
                    {
                        //System.out.println(p.XML); 
                        loadFromXml(p2.XML);
                        lock = false;
                    }
                    else
                    {
                        printError(p2.msg);
                        break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
}
