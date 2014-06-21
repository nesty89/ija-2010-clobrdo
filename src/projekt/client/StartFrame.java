package projekt.client;

import java.io.*;
import java.awt.*;
import java.awt.SystemColor;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Integer.*;
import javax.swing.border.*;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.GroupLayout;
import javax.swing.event.*;

/**
 * Trida startframe - zobrazi okno pro nastaveni nove hry
 * @author nesty
 */
public class StartFrame extends JFrame implements ActionListener {
    protected int playerNum = 2;
    protected int human = 1;
    protected int fields = 36;

    // hlavni obal
    private JFrame startFrame;
    private SpringLayout layout;
    private JPanel butMenu;
    private JPanel spinnerPan;
    private JPanel spinnerPan1;
    private JSpinner spinner;
    private JSpinner spinner1;
    private SpringLayout.Constraints spinnerBounds;
    private JLabel spinnerLabel;
    // tahatko pro nastaveni poctu policek 36 - 72
    private JPanel sliderPan;
    private JPanel pan;
    private JCheckBox check;
    private JTextField name;
    private JTextField host;
    private JTextField port;
    private JTextField plrnum;
    private JComboBox combo;
    private int portnum;
    private int netplayernum;
    private String hosturl;
    private String gamename;
    private int server = 0;
    private int netgame = 0;
    private JSlider slider;
    private SpringLayout.Constraints sliderBounds;
    private JLabel sliderLabel;

    // tlacitka pro spusteni typu hry
    private JButton buttonP1;
    private JButton buttonP2;

    /**
     * konstruktor tridy
     */
    public StartFrame()
    {
        init();
    }

    /**
     * incializace a zobrazeni okna
     */
    public void init()
    {
        startFrame = new JFrame("Nová hra");
        startFrame.addWindowListener(new WindowAdapter() {  // pri zavirani okna zavola metodu konec()
            @Override
            public void windowClosing(WindowEvent e) {
                konec();
            }
        });
        Toolkit obraz = startFrame.getToolkit();
        Dimension velikost_okna = obraz.getScreenSize();
        startFrame.setBounds(velikost_okna.width/2-214, velikost_okna.height/2-250,250,500);
        layout = new SpringLayout();
        startFrame.getContentPane().setLayout(layout);
        JPanel contentTab = new JPanel(layout);
        butMenu = new JPanel();
        SpringLayout.Constraints butMenuBounds = layout.getConstraints(butMenu);
        butMenuBounds.setWidth(Spring.constant(245));
        butMenuBounds.setHeight(Spring.constant(550));
        butMenu.setLayout(new BoxLayout(butMenu,BoxLayout.Y_AXIS));
        startFrame.getContentPane().add(butMenu);
        
        showSpinner();
        showSpinner1();
        showSlider();
        showServerOpts();

        JPanel butPan = new JPanel();
        SpringLayout.Constraints butPanBounds= layout.getConstraints(butPan);
        buttonP1 = new JButton("Start");
        buttonP1.addActionListener(this);

        buttonP2 = new JButton("Zrušit");
        buttonP2.addActionListener(this);
        butPan.add(buttonP1);
        butPan.add(buttonP2);
        butMenu.add(butPan);
        
        startFrame.setResizable(false);
        startFrame.setVisible(true);

    }

    /**
     * zobrazi spinner pro nastaveni poctu hracu
     */
    private void showSpinner()
    {
        spinnerPan1 = new JPanel();
       
        spinnerBounds = layout.getConstraints(spinnerPan1);
        spinnerLabel = new JLabel("Pocet hracu: ");
        spinnerPan1.add(spinnerLabel);
        // spiner naplneni spinneru defaultne na 1, min 1, max 8, posun o 1
        spinner = new JSpinner(new SpinnerNumberModel(2,2,8,1));
        MyChangeListener lst = new MyChangeListener();
        spinner.addChangeListener(lst);
        spinnerPan1.add(spinner);
        butMenu.add(spinnerPan1, BorderLayout.NORTH);
    }

    /**
     * zobrazi spinner pro nastaveni poctu human hracu
     */
    private void showSpinner1()
    {
        spinnerPan = new JPanel();

        spinnerBounds = layout.getConstraints(spinnerPan);
        spinnerLabel = new JLabel("Pocet lidskych hracu: ");
        spinnerPan.add(spinnerLabel);
        // spiner naplneni spinneru defaultne na 1, min 1, max 8, posun o 1
        spinner1 = new JSpinner(new SpinnerNumberModel(1,1,8,1));
        MyChangeListener lst = new MyChangeListener();
        spinner1.addChangeListener(lst);
        spinnerPan.add(spinner1);
        butMenu.add(spinnerPan, BorderLayout.NORTH);
    }

    private void showServerOpts() {
        SpringLayout lay = new SpringLayout();
        pan = new JPanel();
        
        pan.add(check = new JCheckBox("Sitova hra?"));
        String a[] = {"---  Vytvorit  ---", "---  Pripojit   ---", "---  Nacist     ---"};
        pan.add(combo = new JComboBox(a));
        pan.add(new JLabel("Nazev hry: "));
        pan.add(name = new JTextField("",13));
        pan.add(new JLabel("Host (port): "));
        pan.add(host = new JTextField("",9));
        pan.add(port = new JTextField("80",3));
        pan.add(new JLabel("Cislo pripojovaneho hrace: "));
        pan.add(plrnum = new JTextField("",2));
        
//        MyChangeListener lst = new MyChangeListener();
        butMenu.add(pan);
    }

    /**
     * zobrazi slider pro vyber poctu policek
     */
    private void showSlider()
    {

        sliderPan = new JPanel();
        sliderBounds = layout.getConstraints(sliderPan);
       
        // pokus o slider 36 - 72, 36 implicitne
        slider = new JSlider(36, 72, 36);
        MyChangeListener lst = new MyChangeListener();
        slider.addChangeListener(lst);

        slider.setMajorTickSpacing(12);
        slider.setPaintTicks(true); // carky
        slider.setSnapToTicks(true);
        slider.setPaintTrack(true); // lajna na tahatko
        slider.setPaintLabels(true); // hodnota policek
        
        
        sliderLabel = new JLabel("  Policek: " + fields);
        spinnerPan.add(sliderLabel);
        
        sliderPan.add(slider);
        butMenu.add(sliderPan, BorderLayout.CENTER);
    }

    /**
     * obsluhuje akce po kliknuti
     * @param e objekt na ktwery bylo kliknuto
     */
    public void actionPerformed(ActionEvent e) {
        if("Zrušit".equals(e.getActionCommand()))
        {
            startFrame.dispose();
        }
        if("Start".equals(e.getActionCommand()))
        {
            if(playerNum < human) {
                JOptionPane.showMessageDialog(startFrame, "Lidskych hracu muze byt maximalne tolik jako celkovych hracu.");
            }
            else if((playerNum > 4) && (fields == 36))
            {
                JOptionPane.showMessageDialog(startFrame, "Hra s 36 poli lze spustit maximálně se 4 hráči.");
            }
            else
            {
              if(check.isSelected()) {
                  netgame = 1;
                  hosturl = host.getText();
                  portnum = Integer.valueOf(port.getText());
                  gamename = name.getText();
                  netplayernum = Integer.valueOf(plrnum.getText());
                  if(combo.getSelectedItem().equals("---  Vytvorit  ---"))
                      server = 1;
                  else if(combo.getSelectedItem().equals("---  Pripojit   ---"))
                      server = 2;
                  else
                      server = 3;
              } else {
                  netgame = 0;
              }
              //System.out.println("Sit:"+netgame+", hosturl:"+hosturl+", port:"+portnum+", server:"+server);

              new Frame(fields, playerNum, human, null, netgame, hosturl, portnum, server, gamename, netplayernum);
              startFrame.dispose();
            }
        }
    }

    /**
     * listener volany pri kazde zmene slideru nebo spinneru, aktualizuje hodnoty
     */
    class MyChangeListener implements ChangeListener {
      MyChangeListener()
      {}

      public synchronized void stateChanged(ChangeEvent e) {
          Object src = (Object) e.getSource();

          if(src instanceof JSlider)
          {
              JSlider x = (JSlider) src;
              fields = x.getValue();
              sliderLabel.setText(" Policek: " + fields);
          } else if(src instanceof JSpinner) {
              JSpinner x = (JSpinner) src;
              int a = Integer.valueOf(spinner.getValue().toString());
              System.out.println("playernum:"+a+" "+playerNum);
              if(playerNum != a) {
                  playerNum = (Integer) x.getValue();
                  if (playerNum > 8){playerNum = 8;}
                  else if (playerNum < 2){playerNum = 2;}
              } else {
                  human = (Integer) x.getValue();
                  if (human > 8){human = 8;}
                  else if (human < 1){human = 1;}
                  System.out.println("human:"+human);
              }
          }
      }
    }
    public void konec()
    {
      startFrame.dispose();
    }
}
