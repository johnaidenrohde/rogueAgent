import java.lang.*;
import java.util.*;

public class Map {

	public static final char UNVISITED = 'X';
	
	final static int EAST   = 0;
   	final static int NORTH  = 1;
   	final static int WEST   = 2;
   	final static int SOUTH  = 3;


	private char[][] map;
	public int numRows, numCols;


	private Point   axe = null;
	private Point   key = null;
	private Point   gold = null;
	private Vector<Point> dynamite = new Vector<Point>();

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
		map = new char[rows][cols];
		int i, j;
		for (i = 0; i < rows; i++) {
			for (j = 0; j < cols; j++) {
				map[i][j] = UNVISITED;
			}
		}
	}

	// print out larger map while debugging
	public void printMap (Point currPoint) {
		char ch=' ';
		int r,c;
		boolean interestingLine = false;
		System.out.println("\n");
		for( r=0; r < 200; r++ ) {
			for( c=0; c < map[r].length; c++ ) {
	            if(( r == currPoint.row )&&( c == currPoint.col )) { // agent is here
	            	switch( currPoint.dirn ) {
	            		case NORTH: ch = '^'; break;
	            		case EAST:  ch = '>'; break;
	            		case SOUTH: ch = 'v'; break;
	            		case WEST:  ch = '<'; break;
	            	}
	            }
	            else {
	            	ch = map[r][c];
	            }
	            if (ch != 'X') {
	            	System.out.print( ch );
	            	interestingLine = true;
	            }
        	}
        	if (interestingLine) {
        		System.out.println();
        		interestingLine = false;
        	}
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
		}
		new_row = currPoint.row + d_row;
		new_col = currPoint.col + d_col;
         //ch is the object in front of us
		char ch = map[new_row][new_col];
		Point tile = new Point(new_row, new_col, ch);
		return tile;
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