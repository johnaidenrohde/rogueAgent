import java.lang.*;
import java.util.*;

public class Map {

    public static final char UNVISITED = 'X';

    final static int EAST   = 0;
    final static int NORTH  = 1;
    final static int WEST   = 2;
    final static int SOUTH  = 3;
    // diagonals for wallfollowing
    final static int NORTH_EAST = 4;
    final static int SOUTH_EAST = 5;
    final static int SOUTH_WEST = 6;
    final static int NORTH_WEST = 7;


    public char[][] map;
    public int numRows, numCols;


    private Point   axe = null;
    private Point   key = null;
    private Point   gold = null;
    private Vector<Point> dynamite = new Vector<Point>();

    private boolean doorsOpen = false;
    private boolean treesDown = false;
    private boolean unknownOff = false;

    public boolean pointsInit = false;
    private PriorityQueue<Point> pointsToDynamite;

    public Map (int rows, int cols, char initialValue) {
        numRows = rows;
        numCols = cols;
        map = new char[rows][cols];
        int i, j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                map[i][j] = initialValue;
            }
        }
    }

    public Map (int rows, int cols) {
        numRows = rows;
        numCols = cols;
        // initialise to blank char
        this.map = new char[rows][cols];
        int i, j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                map[i][j] = UNVISITED;
            }
        }
    }

    /*******************************************************************************
     * USEFUL FUNCTIONS *
     ******************************************************************************/

    // print out larger map while debugging
    public void printMap (Point currPoint) {
        char ch=' ';
        int r,c;
        boolean interestingLine = false;
        System.out.println("\n");
          // Fiddle with these for different maps
        for( r=75; r < 125; r++ ) {
            System.out.print( (r-0) + "\t");
          // Fiddle with these for different maps
            for( c=50; c < 150; c++ ) {
                if(( r == currPoint.row )&&( c == currPoint.col )) { // agent is here
                    switch( currPoint.dirn ) {
                        case NORTH: ch = '^'; break;
                        case EAST:  ch = '>'; break;
                        case SOUTH: ch = 'v'; break;
                        case WEST:  ch = '<'; break;
                    }
                } else {
                    ch = map[r][c];
                }
                //if (ch != 'X') {
                System.out.print( ch );
                interestingLine = true;
                //}
            }
            //if (interestingLine) {
            System.out.println();
            interestingLine = false;
            //}
        }

        System.out.println();
    }

    public void updateMap(char view[][], Point currPoint) {
        int r=0,c=0,i,j;
        for( i = -2; i <= 2; i++ ) {
            for( j = -2; j <= 2; j++ ) {
                switch( currPoint.dirn ) { // Adjust the orientation
                    case NORTH: r = currPoint.row+i; c = currPoint.col+j; break;
                    case SOUTH: r = currPoint.row-i; c = currPoint.col-j; break;
                    case EAST:  r = currPoint.row+j; c = currPoint.col-i; break;
                    case WEST:  r = currPoint.row-j; c = currPoint.col+i; break;
                }
                switch (view[2+i][2+j]) {
                    case 'd':
                    dynamite.add(new Point(r,c,'d'));
                    break;
                    case 'g':
                    gold = new Point(r,c,'g');
                    break;
                    case 'k':
                    key = new Point(r,c,'k');
                    break;
                    case 'a':
                    axe = new Point (r,c,'a');
                    break;
                }
                map[r][c] = view[2+i][2+j];
            }
        }
        printMap(currPoint);
    }

    public Point getTileInDirection(int direction, Point currPoint) {
        int d_row = 0; int d_col = 0;
        int new_row = 0; int new_col = 0;
        switch (direction) {
            case NORTH:
            d_row = -1; break;
            case SOUTH:
            d_row =  1; break;
            case EAST:
            d_col =  1; break;
            case WEST:
            d_col = -1; break;
            case NORTH_EAST:
            d_row = -1;
            d_col = 1;
            break;
            case NORTH_WEST:
            d_row = -1;
            d_col = -1; break;
            case SOUTH_EAST:
            d_row = 1;
            d_col = 1; break;
            case SOUTH_WEST:
            d_row = 1;
            d_col = -1; break;
        }
        new_row = currPoint.row + d_row;
        new_col = currPoint.col + d_col;
         //ch is the object in front of us
        char ch = map[new_row][new_col];
        Point tile = new Point(new_row, new_col, ch);
        return tile;
    }

    public boolean isWalkable(Point p) {
        char tile = p.value;
        switch(tile) {
            case '*': case 'T': case '-': case '~':
            return false;
            default:
            return true;
        }
    }

   //A dependent path checking function for a star
    public boolean astarIsWalkable(Point p) {
        char tile = p.value;
        if( tile == 'T' && treesDown){
            return(true);
        }else if( tile == '-' && doorsOpen ){
            return true;
        }else if( tile == 'X' && unknownOff ){
            return false;
        } else if (pointsToDynamite.contains(p)) {
            return true;
        } else if( tile == '*' || tile == '~' || tile == 'T' || tile == '-'){
            return false;
        }
      //Otherwise
        return true;
    }

    public void makeWalkable(char c){
        if(c == '-'){
            doorsOpen = true;
        }else if(c == 'T'){
            treesDown = true;
        }else if(c == 'X'){
            unknownOff = true;
        }
    }

    public boolean isAdjacentTo(Point p, Point curr) {
        int dRow, dCol;
        dRow = p.row - curr.row;
        dCol = p.col - curr.col;
        dRow *= dRow;
        dCol *= dCol;
        if (dRow <= 1 && dRow >= -1 && dCol <= 1 && dCol >= -1) {
            return true;
        }
        return false;
    }

    public int getDirection(Point from, Point to) {
        // must be adjacent
        if (!isAdjacentTo(from, to)) {
            return -1;
        }
        int fRow, fCol;
        int tRow, tCol;
        fRow = from.row;
        fCol = from.col;
        tRow = to.row;
        tCol = to.col;
        if (fRow == tRow - 1) {
            if (fCol == tCol) {
                return NORTH;
            } else if (fCol == tCol - 1) {
                return NORTH_EAST;
            } else if (fCol == tCol + 1) {
                return NORTH_WEST;
            }
        } else if (fRow == tRow) {
            if (fCol == tCol - 1) {
                return EAST;
            } else if (fCol == tCol + 1) {
                return WEST;
            } else {
                return -1;
            }
        } else if (fRow == tRow + 1) {
            if (fCol == tCol) {
                return SOUTH;
            } else if (fCol == tCol - 1) {
                return SOUTH_EAST;
            } else if (fCol == tCol + 1) {
                return SOUTH_WEST;
            }
        }
        return -1;
    }

    public void refreshDynamite() {
        if (gold == null) {
            // points go into queue in arbitrary order
            pointsToDynamite = new PriorityQueue<Point>();
        } else {
            // points ordered by man distance
            Comparator<Point> comp = new PointComparator();
            pointsToDynamite = new PriorityQueue<Point>(100, comp);
        }

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (map[i][j] == 'T' || map[i][j] == '*') {
                    // it's dynamitable! add to queue.
                    Point p = new Point(i,j,map[i][j]);
                    findGoldDistance(p);
                    if (!pointsToDynamite.contains(p)){ 
                        pointsToDynamite.add(p);
                    }
                }
            }
        }
        pointsInit = true;
    }

    public Point dynamite() {
        // find dynamitable things radiating out from gold (if exists).
        Point next = pointsToDynamite.poll();
        return next;
    }

    private void findGoldDistance(Point p) {
        // manhattan distance from gold (if exists) to Point p.
        if(gold != null) {
            p.setF(Astar.manDistance(p, gold));
        }
    }

    public Vector<Point> findGroupsX (Point currPoint) {
        // adding points so agent finds groups sanely
        Comparator<Point> comp = new PointComparator();
        PriorityQueue<Point> points = new PriorityQueue<Point>(500, comp);

        // Vector of points, each point is center of first line of X group.
        Vector<Point> x;
        // must only find groups of Xs with blanks on both sides.
        int i;
        for (i = 0; i < numRows; i++) {
            int j;
            boolean hasBlank = false;
            int startX = -1;
            int centreXs = -1;
            for (j = 0; j < numCols; j++) {
                if (map[i][j] == ' ') {
                    hasBlank = true;
                }
                if (hasBlank && map[i][j] == 'X') {
                    startX = j;
                }
                if (hasBlank && startX != -1 && map[i][j] == ' ') {
                    // all criteria for this line to have orphaned blanks true
                    centreXs = startX;//+(j - startX);
                    Point p = new Point(i, centreXs);
                    p.setF(Astar.manDistance(currPoint, p));
                    points.add(p);
                    // reset startX
                    startX = -1;
                }
            } // finished scanning line

        }
        x = new Vector<Point>(points);
        return x;
    }

    public Vector<Point> getTrees (Point currPoint) {
        Vector <Point> x = new Vector<Point>();
        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                if (map[i][j] == 'T') {
                    Point p = new Point(i, j);
                    x.add(p);
                }
            }
        }
        return x;
    }

    public void rewrite(Point p, char toWrite){
        map[p.row][p.col] = toWrite;
    }



   /* Used to remove all of a give item from the map.
    *
    * Called by the action planner.
    */
    public void removeAllItems(char toRemove){
        int i, j;
        for(i =0; i < numRows; i++){
            for(j =0; j < numCols; j++){
                if(map[i][j] == toRemove){
                    map[i][j] = ' ';
                }
            }
        }   
    }

    public void setTile(Point location) {
        map[location.row][location.col] = location.value;
    }

    public Point getTileWithLocation(Point location) {
        int row = location.row;
        int col = location.col;
        Point p = new Point(row, col, map[row][col]);
        return p;
    }

    public Point getTileWithLocation(int row, int col) {
        Point p = new Point(row, col, map[row][col]);
        return p;
    }

        // getters for axe, key, gold, dynamite
        // returns null if none exist
    public Point getAxe() {
        return axe;
    }

    public Point getKey() {
        return key;
    }

    public Point getGold() {
        return gold;
    }

    public Vector<Point> getDynamite() {
        if (dynamite.isEmpty()) {
            return null;
        } else {
            return dynamite;
        }
    }

}
