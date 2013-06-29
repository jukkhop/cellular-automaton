/**
 * @(#)GUI.java
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * .
 *
 * @author Hopeavuori Jukka
 */
public class GUI extends JFrame {
    
    final static int width = 1280;
    final static int height = 960;

    final static int grid_width = 720;
    final static int grid_height = 720;

    private MyFileChooser fileChooser = new MyFileChooser();
    private JMenuItem open, save, quit;
    private JButton start, stop;

    boolean changesMade = false;
    
    /** Save folder path */
    private final String savePath = "./save";

    /** */
    private static Automaton automaton;

    /** */
    private static GridDisplay gridDisplay;

    public GUI() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Sans", Font.BOLD, 13));
        fileMenu.setMnemonic(KeyEvent.VK_T);
        
        // Items for File menu
        open = new JMenuItem("Open", new ImageIcon("../img/open.gif"));
        save = new JMenuItem("Save", new ImageIcon("../img/save.gif"));
        quit = new JMenuItem("Quit");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        
        // Add items to menu and menu to menubar
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.addSeparator();
        fileMenu.add(quit);
        menuBar.add(fileMenu);
        
        // Main panel
        JPanel main_panel = new JPanel(new BorderLayout(4, 4));
        main_panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        // Grid panel
        JPanel grid_panel = new JPanel(new GridLayout(1, 1, 0, 0));
        grid_panel.setBorder(new LineBorder(Color.BLACK, 1));
        grid_panel.setPreferredSize(new Dimension(grid_width, grid_height));

        // South panel
        JPanel south_panel = new JPanel(new GridLayout(1, 1, 0, 0));
        south_panel.setBorder(new LineBorder(Color.BLACK, 1));
        south_panel.setPreferredSize(new Dimension(width, 170));

        // East panel
        JPanel east_panel = new JPanel(new GridLayout(1, 1, 0, 0));
        east_panel.setBorder(new LineBorder(Color.BLACK, 1));
        east_panel.setPreferredSize(new Dimension(width-grid_width-22, 700));

        // Populate grid_panel
        gridDisplay = new GridDisplay(grid_width, grid_height, automaton.getCells());
        grid_panel.add(gridDisplay);

        // Populate east panel
        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");
        east_panel.add(start);
        east_panel.add(stop);

        // Populate main panel
        main_panel.add(grid_panel, BorderLayout.WEST);
        main_panel.add(south_panel, BorderLayout.SOUTH);
        main_panel.add(east_panel, BorderLayout.EAST);

        // Populate frame
        setJMenuBar(menuBar);
        add(main_panel);

        // Listeners
        open.addActionListener(new menuBarListener());
        save.addActionListener(new menuBarListener());
        quit.addActionListener(new menuBarListener());
        this.addWindowListener(new frameListener());

        grid_panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                gridClicked(e);
            }
        });

        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                automaton.start();
            }
        });

        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                automaton.stop();
            }
        });
    }
    
    /**
     * This listener handles the application window events
     */
    class frameListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            quit();
        }
    }

    /**
     * This listener handles the menubar events
     */
    class menuBarListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == open) open();
            if (e.getSource() == save) save();
            if (e.getSource() == quit) quit();
        }
    }

    void gridClicked(MouseEvent e) {
        int x = gridDisplay.coordToPos(e.getX());
        int y = gridDisplay.coordToPos(e.getY());

        Cell c = automaton.getCellAt(x, y);
        if (c==null || c.state==Cell.DEAD) {
            automaton.spawnCell(x, y);
        } else {
            c.state = Cell.DEAD;
        }
    }
    
    class MyFileChooser extends JFileChooser {
        MyFileChooser() {
            super();
            
            if (checkDirectory()) {
                setCurrentDirectory(new File(savePath));
            }
        }
    }

    /**
     * Ensures that the save folder used by the application exists.
     * 
     * @return true if the folder already existed or was created,
     *         false if the folder couldn't be created
     */
    private boolean checkDirectory() {
        File folder = new File(savePath);

        if (!folder.exists()) {
            try {
                folder.mkdir();
                return true;

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(GUI.this,
                "Check that you have write permissions to the folder, " +
                "that you are running for program from.",
                "Folder could not be created!",
                JOptionPane.ERROR_MESSAGE);
                
                return false;
            }

        } else return true;
    }

    private void open() {
        if (changesMade) {
            int option = JOptionPane.showOptionDialog(null, 
            "You have unsaved changes - are you sure you want to quit?", 
            "Confirm quit", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            null, new String[] {"Yes", "No"}, null);
            
            if (option == JOptionPane.YES_OPTION) {
                save();
            }
        }
        
        int option2 = fileChooser.showOpenDialog(GUI.this);
        if (option2 == JFileChooser.APPROVE_OPTION) {
            /*
            try {
                fileChooser.getSelectedFile();
                changesMade = false;

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(GUI.this,
                "Ensure that you have read permissions to the file " +
                "and that the file is valid.",
                "Error opening file!",
                JOptionPane.ERROR_MESSAGE);

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(GUI.this,
                "Varmista, että tiedosto Ottelurekisteri.java löytyy " +
                "hakemistosta, josta ohjelmaa ajetaan.",
                "Ohjelmasta puuttuu tiedostoja!",
                JOptionPane.WARNING_MESSAGE);
            }
            */
        }
    }
    
    private void save() {
        boolean ready = false;
        int option;
        File file = null;
        String path;
        
        while (!ready) {
            option = fileChooser.showSaveDialog(GUI.this);
            
            if (option == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                path = file.getAbsolutePath();

                if (file.exists()) {
                    option = JOptionPane.showOptionDialog(GUI.this, 
                    file.getName() + " already exists - do you want to replace it?", 
                    "Confirm overwrite", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE,
                    null, new String[] {"Yes", "No"}, null);

                    if (option == JOptionPane.YES_OPTION) {
                        ready = true;
                    }
                }
                else ready = true;
            }
            else return;
        }
        
        /*
        try {
            changesMade = false;

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(GUI.this,
            "Varmista, että sinulla on kirjoitusoikeudet hakemistoon, " +
            "johon yritit tallentaa.",
            "Virhe kirjoittaessa tiedostoa!",
            JOptionPane.ERROR_MESSAGE);
        }
        */
    }

    /** 
     * Closes the application, asking confirmation if necessary.
     */
    private void quit() {
        if (changesMade) {
            int option = JOptionPane.showOptionDialog(GUI.this, 
            "Changes have not been saved - are you sure you want to quit?", 
            "Confirm quit", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            null, new String[] {"Yes", "No"}, null);

            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }

        } else { System.exit(0); }
    }
    
    /** 
     * This method creates and shows the application window.
     */
    private static void createAndShowGUI() {
        GUI frame = new GUI();
        frame.setTitle("Cellular automaton");
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
    }
 
    /** Main method which initializes the application. */
    public static void main(String[] args) {

        // Run createAndShowGUI() in Swing Event Dispatch -thread
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

        // Create the automaton and start the simulation
        automaton = new Automaton();
    }
}