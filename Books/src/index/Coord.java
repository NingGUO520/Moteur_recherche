package index;

public class Coord{
	
	protected int line;
	protected int occ;
	
	public Coord(int line, int occ) {
		this.line = line;
		this.occ = occ;
	}
	
	public String toString() {
		return "(" + line + "," + occ + ")";
	}

	public int getLine() {
		return line;
	}

	public int getOcc() {
		return occ;
	}
	
}