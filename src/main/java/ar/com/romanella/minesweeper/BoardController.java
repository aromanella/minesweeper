package ar.com.romanella.minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ar.com.romanella.minesweeper.model.BoardOrientation;
import ar.com.romanella.minesweeper.model.MineCell;

@RestController
public class BoardController {

	MineCell[][] cells;

	String[][] cellsCurrent;
	
	@GetMapping("/api/setup")
	public @ResponseBody ResponseEntity<GameBoard> setup(@RequestParam("x") Integer sizeX, @RequestParam("y") Integer sizeY, @RequestParam("mines") Integer mines) {
		if (sizeX == null || sizeX < 0 || sizeY == null || sizeY < 0) {
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
				
		cells = gb.getCells();
		cellsCurrent = gb.getCellsCurrent();
		
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
		
		return new ResponseEntity<>(gb, HttpStatus.OK);
	}
	
	@PostMapping("/api/play")
	public @ResponseBody String[][] play(@RequestBody MineCell position) {
		int x = position.getX();
		int y = position.getY();
		String cellValue = cellsCurrent[x][y];
		if ("F".equals(cellValue)) {
			return cellsCurrent;
		}
		
		MineCell chosenCell = cells[x][y];
		boolean isMined = true;
		if (isMined) {
			// TODO Impl
		}

		return cellsCurrent;
	}

}
