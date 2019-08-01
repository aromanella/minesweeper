package ar.com.romanella.minesweeper.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Alejandro Romanella
 */
@JsonIgnoreProperties({"cells", "mines"})
public class GameBoard {

	String id;
	
	@Transient
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void generateId() {
		this.id = UUID.randomUUID().toString();
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
