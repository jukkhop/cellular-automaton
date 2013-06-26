/**
 * @(#)GridDisplay.java
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * .
 *
 * @author Hopeavuori Jukka
 */
public class GridDisplay extends JPanel {
    
    private int xCoord = 100;
    private int yCoord = 100;
    private int repaintInterval = 100;

    private ArrayList<Cell> cells;

    public GridDisplay(ArrayList<Cell> cells) {
        this.cells = cells;

        Timer timer = new Timer(repaintInterval, new TimerListener());
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString("juu", xCoord, yCoord);
    }

    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            repaint();
        }
    }
 
}