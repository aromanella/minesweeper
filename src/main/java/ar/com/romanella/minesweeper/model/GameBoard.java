package ar.com.romanella.minesweeper.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Alejandro Romanella
 */
public class GameBoard {

	MineCell[][] cells;

	String[][] cellsCurrent;

	boolean gameOver;
	
	int mines;

	LocalDateTime creationTime;

	String elapsedTime;
	
	public MineCell[][] getCells() {
		return cells;
	}

	public void setCells(MineCell[][] cells) {
		this.cells = cells;
	}

	public String[][] getCellsCurrent() {
		return cellsCurrent;
	}

	public void setCellsCurrent(String[][] cellsCurrent) {
		this.cellsCurrent = cellsCurrent;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public int getMines() {
		return mines;
	}

	public void setMines(int mines) {
		this.mines = mines;
	}
	
		public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
}
