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

    int x, y, state, nextState, age;
    int[][] neighCoords;

    public Cell(int x, int y, int state) {
        this.x = x;
        this.y = y;
        this.state = state;

        age = 0;

        neighCoords = new int[][] { {x, y-1}, {x+1, y-1}, {x+1, y}, {x+1, y+1},
                                    {x, y+1}, {x-1, y+1}, {x-1, y}, {x-1, y-1} };
    }
}