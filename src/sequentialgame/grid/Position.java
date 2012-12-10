package sequentialgame.grid;

/**
 * A position on a 2-dimensional board, defined
 * by a pair of (x,y) coordinates.
 * 
 * @author sodomka
 *
 */
public class Position {
	private int x;
	private int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Position)) return false; 

		Position that = (Position) o;
		if (x != that.x) return false;
		if (y != that.y) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + x;
		result = 37 * result + y;
		return result;
	}

}
