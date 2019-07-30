package ar.com.romanella.minesweeper.model;

/**
 * @author Alejandro Romanella
 */
public enum BoardOrientation {

	NW(-1, -1), N(0, -1), NE(1, -1),
	W(-1, 0), E(1, 0),
	SW(-1, 1), S(0, 1), SE(1, 1);
	
	private int xOffset;
	private int yOffset;
	
	private BoardOrientation(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public int getxOffset() {
		return xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

}
