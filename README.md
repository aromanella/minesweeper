# Minesweeper
A Minesweeper implementation in Java + React, made by Alejandro Romanella

## Operation
The project requires maven and npm. To run it, execute the following commands:
```
mvn clean install
java -jar target/minesw-0.0.1-SNAPSHOT.jar
```

A MongoDB is also needed for saving and loading games. Either MongoDB 3.6 or 4.0 will work. Once installed, the application will attempt to connect to it at localhost in the default port (27017).

No queries are needed for using the application since MongoDB will create the database and collection when they are executed for the first time.

## Implementation Details
- The whole game state is in the Java backend. This was decided to avoid cheating by inspecting the frontend. The client will only get the current game status information necesary for rendering but nothing else.
- MongoDB was selected for persistance since it requires almost no setup compared to other traditional relationship databases like Mysql where tables have to exist before being used.
- Game supports multiple clients at the same time and will track their respective boards by using a UUID generated on game start.
- Color scheme was taken from the original Microsoft Minesweeper game using MS Paint. Mine and flag icons taken from free art on the web.

## Pendings
This is the list of future fixes and general pendings:
- Implement "resume game" functionality (currently the game is saved automatically when it's over)
- Add Spring Security to manage user identification and use a human readable id to identify a game state.
- Add a caching mechanism for removing old games from memory after a proper amount of time.
- Add responsive capabilities to adjust game field according to its size.
- Add JUnit testing to the APIs.

## API endpoints

### /api/setup (GET)
Will start a new game given the board size and number of mines passed as parameters.

**Parameters**
- x (Integer)
- y (Integer)
- mines (Integer)

### /api/play (POST)
Process a cell according the x and y coordinate for a given board id and will return the full status of the board.

**Parameters**
- id (String)
- x (Integer)
- y (Integer)

**Example Reply**
```
{
  "id":"35c994af-1b45-4a66-8e4f-822727db33a7",
  "cellsCurrent":[["1","E","E"],["E","E","2"],["1","E","E"]],
  "gameOver":false,
  "creationTime":"2019-07-31T21:52:22.477",
  "elapsedTime":"00:00:02"
}
```

### /api/flag (POST)
Will attempt to flag or unflag a cell according to the x and y coordinate for a given board id and will return the full status of the board without including additional information since the game cannot be over by flagging cells.

**Parameters**
- id (String)
- x (Integer)
- y (Integer)

**Example Reply**
```
{
  [["1","E","E"],["E","E","2"],["1","F","E"]]
}
```

### /api/loadGame (GET)
Loads the full board status given a valid board id.

**Parameters**
- id (String)
