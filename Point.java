public class Point {

	final static int EAST   = 0;
   	final static int NORTH  = 1;
   	final static int WEST   = 2;
   	final static int SOUTH  = 3;

	public int row;
	public int col;

	public char value;
	public int dirn;

	public Point (int row, int col, char value) {
		this.row = row;
		this.col = col;
		this.value = value;
		dirn = NORTH;
	}

	public Point (int row, int col) {
		this.row = row;
		this.col = col;
		this.value = ' ';
		dirn = NORTH;
	}

}