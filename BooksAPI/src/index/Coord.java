package index;

public class Coord{
	
	protected int line;
	protected int pos;
	
	public Coord(int line, int pos) {
		this.line = line;
		this.pos = pos;
	}
	
	public String toString() {
		return "(" + line + "," + pos + ")";
	}

	public int getLine() {
		return line;
	}

	public int getpos() {
		return pos;
	}
	
}