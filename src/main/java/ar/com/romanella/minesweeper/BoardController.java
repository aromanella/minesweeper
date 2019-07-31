package ar.com.romanella.minesweeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

@RestController
public class BoardController {

	private Map<String, GameBoard> boards = new HashMap<>();
	
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
				cellsCurrent[i][j] = "E";

				j++;
			}
			i++;
			j = 0;
		}

		this.printMaze(id);
		
		return new ResponseEntity<>(gb, HttpStatus.OK);
	}
	
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
		if ("F".equals(cellValue)) {
			return new ResponseEntity<>(board, HttpStatus.OK);
		}
		
		this.calculateElapsedTime(board);
		
		MineCell chosenCell = cells[x][y];
		boolean isMined = chosenCell.isMined();
		if (isMined) {
			this.reveal(id);
			
			cellsCurrent[x][y] = "B";

			board.setGameOver(true);
			
		} else {
			this.checkSurroundings(id, chosenCell);
			boolean flag = this.checkWinConditions(id);
			board.setGameOver(flag);
		}

		return new ResponseEntity<>(board, HttpStatus.OK);
	}

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
		if ("E".equals(chosenCell)) {
			cellsCurrent[x][y] = "F";
		} else if ("F".equals(chosenCell)) {
			cellsCurrent[x][y] = "E";
		}

		return cellsCurrent;
	}
	
	private boolean checkWinConditions(String id) {
		GameBoard board = this.boards.get(id);
		MineCell[][] cells = board.getCells();
		String[][] cellsCurrent = board.getCellsCurrent();
		
		for (MineCell[] cellRow : cells) {
			for (MineCell cell : cellRow) {
				int x = cell.getX();
				int y = cell.getY();
				String value = cellsCurrent[x][y];
				if ("E".equals(value) && !cell.isMined()) {
					return false;
				}
			}
		}
		System.out.println("WON !");
		
		return true;
	}
	
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
}
