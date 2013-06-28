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
    
    private int width;
    private int height;

    private int size = 48;
    private int repaintInterval = 16;

    private ArrayList<Cell> cells;

    private int squareSize, center;

    public GridDisplay(int width, int height, ArrayList<Cell> cells) {
        this.width  = width;
        this.height = height;
        this.cells  = cells;

        setSize(size);
        setBackground(Color.WHITE);

        Timer timer = new Timer(repaintInterval, new TimerListener());
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(100, 100, 100));
        int pos;
        for (int i=1 ; i<size ; i++) {
            pos = i*squareSize;
            g.drawLine(pos, 0, pos, height);
            g.drawLine(0, pos, width, pos);
        }

        g.setColor(Color.BLACK);
        int x, y;
        for (Cell cell : cells) {
            if (cell.state == Cell.DEAD) continue;
            x = center + cell.x;
            y = center + cell.y;
            g.fillRect(x*squareSize, y*squareSize, squareSize, squareSize);
        }
    }

    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            repaint();
        }
    }

    public void setSize(int size) {
        this.size = size;
        squareSize = width / size;
        center = (int) Math.floor(size / 2) - 1;
    }

    public int coordToPos(int coord) {
        int centerCoord = (center+1) * squareSize;
        if (coord > centerCoord) {
            return (coord-centerCoord)/squareSize + 1;
        } else {
            return (centerCoord-coord)/squareSize * -1;
        }
    }
 
}