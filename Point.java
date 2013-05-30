public class Point {

	final static int EAST   = 0;
	final static int NORTH  = 1;
	final static int WEST   = 2;
	final static int SOUTH  = 3;

<<<<<<< HEAD
   // A link to another Point for use in the Astar pathfinding function
	public Point parent;
=======
   // Variables for use in the Astar
   public Point parent;
   public int f;
   public int g;
>>>>>>> 81dc39f76f4524c44f3fd8f90ff4f715ba3628fb

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
=======
   public void setF (int f){
      this.f = f;
   }

   public void setG (int g){
      this.g = g;
   }

   public void setParent (Point parent){
      this.parent = parent;
   }
>>>>>>> 81dc39f76f4524c44f3fd8f90ff4f715ba3628fb
}
