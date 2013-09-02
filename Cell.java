/**
 * @(#)Cell.java
 */



/**
 * .
 *
 * @author Hopeavuori Jukka
 */
public class Cell {
    
    public static final boolean ALIVE = true;
    public static final boolean DEAD  = false;

    int x, y;
    boolean state, nextState, processed;

    public Cell(int x, int y, boolean state) {
        this.x = x;
        this.y = y;
        this.state = state;

        processed = false;
    }
}