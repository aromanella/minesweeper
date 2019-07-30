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

	@GetMapping("/api/setup")
	@ResponseBody
	public String[][] setup(@RequestParam("x") Integer sizeX, @RequestParam("y") Integer sizeY) {
		cells = new MineCell[sizeX][sizeY];
		
		Random r = new Random(System.currentTimeMillis());
		List<MineCell> list = new ArrayList<>();

		for (int counter = 1; counter <= 10; counter++) {
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
		
		return null;
	}

}
