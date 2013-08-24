/**
 * @(#)Cell.java
 */



/**
 * .
 *
 * @author Hopeavuori Jukka
 */
public class Cell {
    
    public static final int ALIVE = 1;
    public static final int DEAD  = 0; 

    int x, y, state, nextState;

    public Cell(int x, int y, int state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }
}