public class Point {

	public int row;
	public int col;

	public char value;

	public Point (int row, int col, char value) {
		this.row = row;
		this.col = col;
		this.value = value;
	}

	public Point (int row, int col) {
		this.row = row;
		this.col = col;
		this.value = ' ';
	}

}