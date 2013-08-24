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

    public static final int ON  = 1;
    public static final int OFF = 0; 

    private int state = OFF;
    private int tickInterval = 400;
    private int tickCount = 0;

    // Cells in the automaton
    private ArrayList<Cell> cells;

    // Cell Birth/Survival rules (bitmasked)
    private int B_RULE, S_RULE;
    private static final int[] FLAGS = {1, 2, 4, 8, 16, 32, 64, 128, 256};

    // 
    private Timer timer;

    public Automaton() {
        cells = new ArrayList<Cell>();
    }

    public void setRules(String B, String S) {
        B_RULE = maskRules(B);
        S_RULE = maskRules(S);
    }

    public String[] getRules() {
        return new String[]{unmaskRules(B_RULE), unmaskRules(S_RULE)};
    }

    private int maskRules(String r) {
        int m = 0;
        int n;
        for (char c : r.toCharArray()) {
             n = Math.max(Math.min(Character.getNumericValue(c), 8), 0);
             m += FLAGS[n];
        }
        return m;
    }

    private String unmaskRules(int r) {
        String s = "";
        for (int i=0 ; i<=8 ; i++) {
            if ((r & FLAGS[i]) != 0) s += i;
        }
        return s;
    }

    public void spawnCell(int x, int y) {
        boolean exists = false;
        for (Cell cell : cells) {
            if (x==cell.x && y==cell.y) {
                cell.state = Cell.ALIVE;
                exists = true;
            }
        }
        if (!exists) cells.add(new Cell(x, y, Cell.ALIVE));

        int[][] neighs = {  {x, y-1}, {x+1, y-1}, {x+1, y}, {x+1, y+1},
                            {x, y+1}, {x-1, y+1}, {x-1, y}, {x-1, y-1}
                         };

        for (int i=0 ; i<neighs.length ; i++) {
            exists = false;
            for (Cell cell : cells) {
                if (neighs[i][0] == cell.x && neighs[i][1] == cell.y) {
                    exists = true;
                }
            }
            if (!exists) {
                cells.add(new Cell(neighs[i][0], neighs[i][1], Cell.DEAD));
            }
        }
    }

    private void tick() {
        tickCount++;
        for (Cell cell : cells) {
            int n = countNeighbours(cell);
            if (cell.state == Cell.ALIVE) {
                cell.nextState = ((S_RULE&FLAGS[n]) != 0) ? Cell.ALIVE : Cell.DEAD;
            }
            if (cell.state == Cell.DEAD) {
                cell.nextState = ((B_RULE&FLAGS[n]) != 0) ? Cell.ALIVE : Cell.DEAD;
            }
        }

        ArrayList<Cell> cells2 = new ArrayList<Cell>();
        for (Cell cell : cells) cells2.add(cell);

        for (Cell cell : cells2) {
            if (cell.state==Cell.DEAD && cell.nextState==Cell.ALIVE) {
                spawnCell(cell.x, cell.y);
            }
            cell.state = cell.nextState;
        }
    }

    private int countNeighbours(Cell c) {
        int n = 0;
        for (Cell cell : cells) {
            if (cell==c || cell.state==Cell.DEAD) continue;
            int xd = cell.x - c.x;
            int yd = cell.y - c.y;
            if (Math.sqrt(xd*xd + yd*yd) < 2) n++;
        }
        return n;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public Cell getCellAt(int x, int y) {
        for (Cell c : cells) {
            if (c.x==x && c.y==y) return c;
        }
        return null;
    }

    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            tick();
        }
    }

    public boolean start() {
        if (state == OFF) {
            timer = new Timer(tickInterval, new TimerListener());
            timer.start();
            state = ON;
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (state == ON) {
            timer.stop();
            state = OFF;
            return true;
        }
        return false;
    }

    public String toString() {
        String s = "";
        for (Cell c : cells) {
            if (c.state == Cell.ALIVE) s += c.x+" "+c.y+"\n";
        }
        return s;
    }
}