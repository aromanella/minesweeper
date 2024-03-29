package ar.com.romanella.minesweeper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ar.com.romanella.minesweeper.model.BoardOrientation;
import ar.com.romanella.minesweeper.model.GameBoard;
import ar.com.romanella.minesweeper.model.MineCell;
import ar.com.romanella.minesweeper.service.BoardService;

/**
 * 
 * @author Alejandro Romanella
 *
 */
@RestController
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	private Map<String, GameBoard> boards = new HashMap<>();
	
	//	TODO convert these constants to an enum
	private static final String MINED = "M";
	private static final String EMPTY = "E";
	private static final String BOMB = "B";
	private static final String FLAG = "F";
	
	/**
	 * This webservice setups a new game from the scratch given the (validated) input parameters.
	 * A UUID is generated as the id for the game which is used to identify the gaming session.
	 * 
	 * @param sizeX	The number of rows
	 * @param sizeY The number of columns
	 * @param mines The number of mines
	 * @return
	 */
	@GetMapping("/api/setup")
	public @ResponseBody ResponseEntity<GameBoard> setup(@RequestParam("x") Integer sizeX, @RequestParam("y") Integer sizeY, @RequestParam("mines") Integer mines) {
		if (sizeX == null || sizeX <= 0 || sizeY == null || sizeY <= 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
		int totalCells = sizeX * sizeY;
		if (mines == null || mines >= totalCells) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
		GameBoard gb = new GameBoard();
		gb.setGameOver(false);
		
		gb.setCells(new MineCell[sizeX][sizeY]);
		gb.setCellsCurrent(new String[sizeX][sizeY]);
		gb.setMines(mines);
		gb.generateId();
		String id = gb.getId();
		
		gb.setCreationTime(LocalDateTime.now());
		
		this.boards.put(id, gb);
		
		MineCell[][] cells = gb.getCells();
		String[][] cellsCurrent = gb.getCellsCurrent();
		
		Random r = new Random(System.currentTimeMillis());
		List<MineCell> list = new ArrayList<>();
		
		for (int counter = 1; counter <= mines; counter++) {
			int x = r.nextInt(sizeX);
			int y = r.nextInt(sizeY);
			MineCell mc = new MineCell(true, x, y);
			while (list.contains(mc)) {
				x = r.nextInt(sizeX);
				y = r.nextInt(sizeY);
				mc = new MineCell(true, x, y);
			}
			list.add(mc);
		}

		for (MineCell mineCell : list) {
			int x = mineCell.getX();
			int y = mineCell.getY();
			cells[x][y] = mineCell;
		}

		int i = 0;
		int j = 0;
		for (MineCell[] cellRow : cells) {
			for (MineCell cell : cellRow) {
				if (cell == null) {
					cells[i][j] = new MineCell(false, i, j);
				}
				cellsCurrent[i][j] = EMPTY;

				j++;
			}
			i++;
			j = 0;
		}

		this.printMaze(id);
		
		return new ResponseEntity<>(gb, HttpStatus.OK);
	}

	/**
	 * Entrypoint for playing the game. Will flag the game as over if a bomb is clicked or if the game is won.
	 * In case of game over, the game is automatically saved for future reviewing.
	 * 
	 * @param position Represents the coordinates of the cell clicked by the player
	 * @return
	 */
	@PostMapping("/api/play")
	public @ResponseBody ResponseEntity<GameBoard> play(@RequestBody MineCell position) {
		String id = position.getId();
		GameBoard board = this.boards.get(id);
		
		boolean gameOver = board.isGameOver();
		MineCell[][] cells = board.getCells();
		String[][] cellsCurrent = board.getCellsCurrent();
		if (gameOver) {
			return new ResponseEntity<>(board, HttpStatus.OK);
		}
		
		int x = position.getX();
		int y = position.getY();
		String cellValue = cellsCurrent[x][y];
		if (FLAG.equals(cellValue)) {
			return new ResponseEntity<>(board, HttpStatus.OK);
		}
		
		this.calculateElapsedTime(board);
		
		MineCell chosenCell = cells[x][y];
		boolean isMined = chosenCell.isMined();
		if (isMined) {
			this.reveal(id);
			
			cellsCurrent[x][y] = BOMB;

			board.setGameOver(true);
			
			this.boardService.saveGame(board);
			
		} else {
			this.checkSurroundings(id, chosenCell);
			boolean flag = this.checkWinConditions(id);
			board.setGameOver(flag);
			
			if (flag) {
				this.boardService.saveGame(board);
			}
		}

		return new ResponseEntity<>(board, HttpStatus.OK);
	}

	/**
	 * This entrypoint will flag or unflag a gameboard cell, only if it wasn't uncovered in the first place.
	 * 
	 * @param position	The coordinates for the cell that was selected to be flagged
	 * @return
	 */
	@PostMapping("/api/flag")
	public @ResponseBody String[][] flag(@RequestBody MineCell position) {
		String id = position.getId();
		GameBoard board = this.boards.get(id);
		
		boolean gameOver = board.isGameOver();
		String[][] cellsCurrent = board.getCellsCurrent();
		
		if (gameOver) {
			return cellsCurrent;
		}
		
		int x = position.getX();
		int y = position.getY();
		
		String chosenCell = cellsCurrent[x][y];
		if (EMPTY.equals(chosenCell)) {
			cellsCurrent[x][y] = FLAG;
		} else if (FLAG.equals(chosenCell)) {
			cellsCurrent[x][y] = EMPTY;
		}

		return cellsCurrent;
	}
	
	/**
	 * 	Entrypoint to load an old game board. The full UUID must be completed in order to find the game.
	 * 	TODO	Implement proper resume logic
	 * 
	 * @param id	The game id that was previously saved
	 * @return
	 */
	@GetMapping("/api/loadGame")
	public @ResponseBody ResponseEntity<GameBoard> loadGame(@RequestParam String id) {
		GameBoard board = this.boardService.loadGame(id.trim());
		if (board == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
		return new ResponseEntity<>(board, HttpStatus.OK);
	}

	/**
	 * Reveal the selected cell. If it has no mines, recursively reveal all of the surrounding ones.
	 * 
	 * @param id
	 */
	private void reveal(String id) {
		GameBoard board = this.boards.get(id);
		MineCell[][] cells = board.getCells();
		String[][] cellsCurrent = board.getCellsCurrent();
		
		for (MineCell[] cellRow : cells) {
			for (MineCell cell : cellRow) {
				int x = cell.getX();
				int y = cell.getY();
				if (cell.isMined()) {
					cellsCurrent[x][y] = MINED;
				} else {
					this.checkSurroundings(id, cell);
				}
			}
		}
	}
	
	/**
	 * Detects if a game is in a winning state
	 * 
	 * @param id
	 * @return
	 */
	private boolean checkWinConditions(String id) {
		GameBoard board = this.boards.get(id);
		MineCell[][] cells = board.getCells();
		String[][] cellsCurrent = board.getCellsCurrent();
		
		for (MineCell[] cellRow : cells) {
			for (MineCell cell : cellRow) {
				int x = cell.getX();
				int y = cell.getY();
				String value = cellsCurrent[x][y];
				if (EMPTY.equals(value) && !cell.isMined()) {
					return false;
				}
			}
		}
		
		return true;
	}

	/**
	 * Sets the total elapsed time since the board was created.
	 * 
	 * @param board
	 */
	private void calculateElapsedTime(GameBoard board) {
		LocalDateTime started = board.getCreationTime();
		LocalDateTime now = LocalDateTime.now();
		
		Duration dur = Duration.between(started, now);
		long millis = dur.toMillis();
		String duration = String.format("%02d:%02d:%02d", 
		        TimeUnit.MILLISECONDS.toHours(millis),
		        TimeUnit.MILLISECONDS.toMinutes(millis) - 
		        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
		        TimeUnit.MILLISECONDS.toSeconds(millis) - 
		        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		
		board.setElapsedTime(duration);
	}
	
	private void checkSurroundings(String id, MineCell chosenCell) {
		GameBoard board = this.boards.get(id);
		MineCell[][] cells = board.getCells();
		String[][] cellsCurrent = board.getCellsCurrent();
		
		int x = chosenCell.getX();
		int y = chosenCell.getY();

		int xMin = 0;
		int yMin = 0;
		int xMax = cells.length;
		int yMax = cells[0].length;

		int total = this.calculateMinesByPosition(id, x, y, xMin, xMax, yMin, yMax);
		
		cellsCurrent[x][y] = String.valueOf(total);

		if (total == 0) {
			BoardOrientation[] around = BoardOrientation.values();
			for (BoardOrientation orientation: around) {
				int xOffset = orientation.getxOffset() + x;
				int yOffset = orientation.getyOffset() + y;
				
				int nextTotal = this.calculateMinesByPosition(id, x, y, xMin, xMax, yMin, yMax);
				if (nextTotal == 0) {
					boolean validPosition = (xOffset >= xMin && xOffset < xMax && yOffset >= yMin && yOffset < yMax);
					if (validPosition) {
						MineCell nextCell = cells[xOffset][yOffset];
						if (!nextCell.isProcessed()) {
							cellsCurrent[xOffset][yOffset] = "0";
							nextCell.setProcessed(true);
							
							checkSurroundings(id, nextCell);
						}
					}
				}
			}
		}
	}

	private int calculateMinesByPosition(String id, int x, int y, int xMin, int xMax, int yMin, int yMax) {
		int total = 0;
		BoardOrientation[] around = BoardOrientation.values();
		for (BoardOrientation orientation : around) {
			boolean isMined = this.isPositionMined(id, orientation, x, y, xMin, xMax, yMin, yMax);
			if (isMined) {
				total++;
			}
		}
		
		return total;
	}
	
	private boolean isPositionMined(String id, BoardOrientation orientation, int x, int y, int xMin, int xMax, int yMin, int yMax) {
		GameBoard board = this.boards.get(id);
		MineCell[][] cells = board.getCells();
		
		int xOffset = orientation.getxOffset() + x;
		int yOffset = orientation.getyOffset() + y;

		boolean isMined = false;
		boolean validPosition = (xOffset >= xMin && xOffset < xMax && yOffset >= yMin && yOffset < yMax);
		if (validPosition) {
			MineCell cell = cells[xOffset][yOffset];
			isMined = cell.isMined();
		}
		
		return isMined;
	}

	/**
	 * Test method used to check solution
	 */
	public void printMaze(String id) {
		GameBoard board = this.boards.get(id);
		MineCell[][] cells = board.getCells();
		
		System.out.println();
		for (MineCell[] cellRow : cells) {
			for (MineCell cell : cellRow) {
				System.out.print(cell.isMined() ? "X" : "-");
			}
			System.out.println();
		}
	}

	public BoardService getBoardService() {
		return boardService;
	}

	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}

	public Map<String, GameBoard> getBoards() {
		return boards;
	}

	public void setBoards(Map<String, GameBoard> boards) {
		this.boards = boards;
	}
}
