public class Point {

	final static int EAST   = 0;
	final static int NORTH  = 1;
	final static int WEST   = 2;
	final static int SOUTH  = 3;

<<<<<<< HEAD
   // A link to another Point for use in the Astar pathfinding function
	public Point parent;
   // Variables for use in the Astar
   public Point parent;
   public int f;
   public int g;
=======

   // A link to another Point for use in the Astar pathfinding function
	public Point parent;
	
	public int f;
	public int g;
>>>>>>> 1d9e157a1d49d376029f3c5bc938f81a52217eb3

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

<<<<<<< HEAD
	public void setParent (Point parent){
		this.parent = parent;
	}
   public void setF (int f){
      this.f = f;
   }
=======

	public void setParent (Point parent){
		this.parent = parent;
	}
>>>>>>> 1d9e157a1d49d376029f3c5bc938f81a52217eb3

	public void setF (int f){
		this.f = f;
	}

	public void setG (int g){
		this.g = g;
	}

}
