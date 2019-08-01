package ar.com.romanella.minesweeper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.romanella.minesweeper.model.GameBoard;
import ar.com.romanella.minesweeper.repository.BoardRepository;

@Service
/**
 *	Database access service 
 * 
 * @author Alejandro Romanella
 *
 */
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;
	
	/**
	 * Save the full game status
	 * 
	 * @param board
	 */
	public void saveGame(GameBoard board) {
		this.boardRepository.saveGame(board);
	}
	
	/**
	 * Load the game status by game id
	 * 
	 * @param id The UUID generated
	 * @return The board or null if not found
	 */
	public GameBoard loadGame(String id) {
		return this.boardRepository.loadGame(id);
	}

	public BoardRepository getBoardRepository() {
		return boardRepository;
	}

	public void setBoardRepository(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}
}
