package ar.com.romanella.minesweeper.model;

/**
 * @author Alejandro Romanella
 */
public class MineCell {

	private String id;
	private boolean mined;
	private int x;
	private int y;
	private boolean processed;
	
	public MineCell(boolean mined, int x, int y) {
		super();
		
		this.mined = mined;
		this.x = x;
		this.y = y;
		this.processed = false;
	}
	
	public MineCell() {
		super();
	}

	public boolean isMined() {
		return mined;
	}
	public void setMined(boolean mined) {
		this.mined = mined;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MineCell other = (MineCell) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MineCell [mined=" + mined + ", x=" + x + ", y=" + y + "]";
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
