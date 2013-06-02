public class Point {

	final static int EAST   = 0;
	final static int NORTH  = 1;
	final static int WEST   = 2;
	final static int SOUTH  = 3;

   // Variables for use in the Astar
   public Point parent;
   public int f;
   public int g;

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
   @Override
   public boolean equals (Object o) {
 	   if (o instanceof Point) {
 	   Point p = (Point) o;
 	   return row == p.row && col == p.col;
 	}
 	   return false;
   }

	public void setParent (Point parent){
		this.parent = parent;
	}
   public void setF (int f){
      this.f = f;
   }

	public void setG (int g){
		this.g = g;
	}

}
