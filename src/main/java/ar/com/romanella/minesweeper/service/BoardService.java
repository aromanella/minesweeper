package ar.com.romanella.minesweeper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.romanella.minesweeper.model.GameBoard;
import ar.com.romanella.minesweeper.repository.BoardRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;
	
	public void saveGame(GameBoard board) {
		this.boardRepository.saveGame(board);
	}
	
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
