public class Point {

	final static int EAST   = 0;
   final static int NORTH  = 1;
   final static int WEST   = 2;
   final static int SOUTH  = 3;

   // A link to another Point for use in the Astar pathfinding function
   public Point parent;

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

   public Point (int row, int col, Point parent) {
		this.row = row;
		this.col = col;
		this.parent = parent;
		dirn = NORTH;
	}

	public Point (int row, int col) {
		this.row = row;
		this.col = col;
		this.value = ' ';
		dirn = NORTH;
	}

   public void setParent (Point parent){
      this.parent = parent;
   }
}
