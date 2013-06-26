/**
 * @(#)Automaton.java
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
public class Automaton {
    
    private int tickInterval = 380;
    private int tickCount = 0;

    // Cells in the automaton
    private ArrayList<Cell> cells;

    public Automaton() {
        cells = new ArrayList<Cell>();
    }

    public void spawnCell(int x, int y) {
        boolean existsAlready = false;
        for (Cell cell : cells) {
            if (x == cell.x && y == cell.y) {
                cell.state = Cell.ALIVE;
                existsAlready = true;
            }
        }
        if (!existsAlready) cells.add(new Cell(x, y, Cell.ALIVE));

        int[][] neighs = { {  x, y-1}, {x+1, y-1}, {x+1, y  }, {x+1, y+1},
                           {  x, y+1}, {x-1, y+1}, {x-1, y  }, {x-1, y-1}
                         };

        for (int i=0 ; i<neighs.length ; i++) {
            boolean neighExists = false;
            for (Cell cell : cells) {
                if (neighs[i][0] == cell.x && neighs[i][1] == cell.y) {
                    neighExists = true;
                }
            }
            if (!neighExists) {
                cells.add(new Cell(neighs[i][0], neighs[i][1], Cell.DEAD));
            }
        }
    }

    private void tick() {
        tickCount++;
        for (Cell cell : cells) {
            int n = countNeighbours(cell);
            if (cell.state == Cell.ALIVE) {
                cell.nextState = (n<2 || n>3) ? Cell.DEAD : Cell.ALIVE;
            }
            if (cell.state == Cell.DEAD) {
                cell.nextState = (n == 3) ? Cell.ALIVE : Cell.DEAD;
            }
        }

        ArrayList<Cell> cells2 = new ArrayList<Cell>();
        for (Cell cell : cells) cells2.add(cell);

        for (Cell cell : cells2) {
            if (cell.state == Cell.DEAD && cell.nextState == Cell.ALIVE) {
                spawnCell(cell.x, cell.y);
            }
            cell.state = cell.nextState;
        }
    }

    private int countNeighbours(Cell c) {
        int n = 0;
        for (Cell cell : cells) {
            if (cell == c || cell.state == Cell.DEAD) continue;
            int xd = cell.x - c.x;
            int yd = cell.y - c.y;
            if (Math.sqrt(xd*xd + yd*yd) < 2) n++;
        }
        return n;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public void start() {
        Timer timer = new Timer(tickInterval, new TimerListener());
        timer.start();
    }

    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            tick();
        }
    }
 
}