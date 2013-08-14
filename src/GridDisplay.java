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
    private ArrayList<Cell> cells;

    private int drawGrill = 1;
    private int squareSize = 20;
    private int repaintInterval = 16;

    private int size, center;

    public GridDisplay(int width, int height, ArrayList<Cell> cells) {
        this.width  = width;
        this.height = height;
        this.cells  = cells;

        setSquareSize(squareSize);
        setBackground(Color.WHITE);

        Timer timer = new Timer(repaintInterval, new TimerListener());
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the grill
        if (drawGrill == 1) {
            g.setColor(new Color(100, 100, 100));
            int pos;
            for (int i=1 ; i<=size ; i++) {
                pos = i*squareSize;
                g.drawLine(pos, 0, pos, height);
                g.drawLine(0, pos, width, pos);
            }
        }

        // Draw the cells
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

    public void setSquareSize(int squareSize) {
        if (squareSize < 1) squareSize = 1;
        this.squareSize = squareSize;
        size = width / squareSize;
        center = size/2;
    }

    public int getSquareSize() {
        return squareSize;
    }

    public int coordToPos(int coord) {
        int centerCoord = (center) * squareSize;
        if (coord > centerCoord) {
            return (coord-centerCoord)/squareSize;
        } else {
            return ((centerCoord-coord)/squareSize * -1) -1;
        }
    }

    public void toggleGrill() {
        drawGrill = (drawGrill==1) ? 0 : 1;
    }
 
}