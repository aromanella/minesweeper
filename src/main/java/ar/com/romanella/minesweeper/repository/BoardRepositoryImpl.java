package ar.com.romanella.minesweeper.repository;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClient;

import ar.com.romanella.minesweeper.model.GameBoard;

@Repository
public class BoardRepositoryImpl implements BoardRepository {

	//	TODO pass this to Spring properties file, or embed in webserver properties
	private MongoClient mongoClient = new MongoClient("localhost", 27017);
	
	@Override
	public void saveGame(GameBoard board) {
		MongoTemplate mongoOps = new MongoTemplate(mongoClient, "minesweeper");
		
		mongoOps.insert(board);
	}
	
	@Override
	public GameBoard loadGame(String id) {
		MongoTemplate mongoOps = new MongoTemplate(mongoClient, "minesweeper");
		
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		
		List<GameBoard> list = mongoOps.find(query, GameBoard.class);
		if (list != null && !list.isEmpty()) {
			GameBoard board = list.get(0);
			return board;
		}
		
		return null;
	}
}
