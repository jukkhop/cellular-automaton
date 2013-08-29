/**
 * @(#)Automaton.java
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

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

    /** A map of all cells in the automaton */
    HashMap<Coords, Cell> cellMap;

    /** List of alive cells */
    private ArrayList<Cell> cells;

    /** Cell Birth/Survival rules (bitmasked) */
    private int B_RULE, S_RULE;
    /** Flags for the rules */
    private static final int[] FLAGS = {1, 2, 4, 8, 16, 32, 64, 128, 256};

    // 
    private Timer timer;

    public Automaton() {
        cellMap = new HashMap<Coords, Cell>();
        cells = new ArrayList<Cell>();
    }

    private Cell addCell(int x, int y, int state) {
        Cell c = new Cell(x, y, state);
        cellMap.put(new Coords(x, y), c);
        return c;
    }

    public void spawnCell(int x, int y) {
        Cell c = getCellAt(x, y);
        if (c == null) {
            c = addCell(x, y, Cell.ALIVE);
        } else {
            c.state = Cell.ALIVE;
        }
        cells.add(c);
    }

    private void tick() {
        tickCount++;

        ArrayList<Cell> changingCells = new ArrayList<Cell>();
        ArrayList<Cell> aliveCells = new ArrayList<Cell>();

        for (Cell cell : cells) {
            ArrayList<Cell> block = getNeighbours(cell);
            block.add(cell);
            
            for (Cell c : block) {
                if (c.age == tickCount) continue;

                c.nextState = getNextState(c);
                c.age = tickCount;

                if (c.state != c.nextState)
                    changingCells.add(c);

                if (c.nextState == Cell.ALIVE)
                    aliveCells.add(c);
            }
        }

        // Change the necessary cell states
        for (Cell c : changingCells) c.state = c.nextState;

        // Replace cells with those that will be alive next gen
        cells = aliveCells;
    }

    private int getNextState(Cell c) {
        int n = countNeighbours(c);
        int nextState = -1;
        if (c.state == Cell.ALIVE)
            nextState = ((S_RULE&FLAGS[n]) != 0) ? Cell.ALIVE : Cell.DEAD;
        
        if (c.state == Cell.DEAD)
            nextState = ((B_RULE&FLAGS[n]) != 0) ? Cell.ALIVE : Cell.DEAD;

        return nextState;
    }

    private ArrayList<Cell> getNeighbours(Cell c) {
        ArrayList<Cell> neighs = new ArrayList<Cell>(8);

        for (int[] coords : c.neighCoords) {
            Cell n = getCellAt(coords[0], coords[1]);
            if (n == null)
                n = addCell(coords[0], coords[1], Cell.DEAD);

            neighs.add(n);
        }
        return neighs;
    }

    private int countNeighbours(Cell c) {
        int n = 0;
        for (Cell neigh : getNeighbours(c)) {
            if (neigh.state == Cell.ALIVE) n++;
        }
        return n;
    }

    public Cell getCellAt(int x, int y) {
        return cellMap.get(new Coords(x, y));
    }

    public void setRules(String B, String S) {
        B_RULE = maskRule(B);
        S_RULE = maskRule(S);
    }

    public String[] getRules() {
        return new String[]{unmaskRule(B_RULE), unmaskRule(S_RULE)};
    }

    private int maskRule(String r) {
        int m = 0;
        int n;
        for (char c : r.toCharArray()) {
             n = Math.max(Math.min(Character.getNumericValue(c), 8), 0);
             m += FLAGS[n];
        }
        return m;
    }

    private String unmaskRule(int r) {
        String s = "";
        for (int i=0; i<=8; i++)
            if ((r & FLAGS[i]) != 0) s += i;
        
        return s;
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

    public void reset() {
        cellMap.clear();
        cells.clear();
        tickCount = 0;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public int getTickCount() {
        return tickCount;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = Math.max(10, tickInterval);
        stop();
        start();
    }

    public String toString() {
        String s = "";
        for (Cell c : cells)
            if (c.state == Cell.ALIVE) s += c.x+" "+c.y+"\n";
        
        return s;
    }

    final class Coords {
        private final int x;
        private final int y;

        public Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Object other) {
            Coords that = (Coords) other;
            return (this.x==that.x && this.y==that.y); 
        }

        public int hashCode() {
            return x * 31 + y;
        }
    }
}