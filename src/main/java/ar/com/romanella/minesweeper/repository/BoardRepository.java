package ar.com.romanella.minesweeper.repository;

import ar.com.romanella.minesweeper.model.GameBoard;

public interface BoardRepository {

	public void saveGame(GameBoard board);
	
	public GameBoard loadGame(String id);
}
