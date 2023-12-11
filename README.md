# Minesweeper Multiplayer Game

## Overview

This Minesweeper project in Java offers a multiplayer gaming experience with server-client interaction. The project consists of several classes managing game logic, user interfaces, and server functionalities.

## Classes

- [Matrix](#matrix)
- [Case](#case)
- [Level](#level)
- [GUI](#gui)
- [Main](#main)
- [Server](#server)
- [ServerGUI](#servergui)
- [ClientHandler](#clienthandler)

# Matrix

Represents the mines matrix.

## Fields

| Type          | Field Name  | Description                                          |
| ------------- | ----------- | ---------------------------------------------------- |
| `boolean[][]` | `cases`     | Two-dimensional array representing the mines matrix. |
| `int`         | `nbMines`   | The number of mines in the matrix.                   |
| `int`         | `dimSquare` | The dimension of the matrix.                         |

## Constructors

| Signature                            | Description                                                                     |
| ------------------------------------ | ------------------------------------------------------------------------------- |
| `Matrix()`                           | Constructs a Matrix instance with default settings.                             |
| `Matrix(int nbMines)`                | Constructs a Matrix instance with the specified number of mines.                |
| `Matrix(int nbMines, int dimSquare)` | Constructs a Matrix instance with the specified number of mines and dimension.  |
| `Matrix(Level level, int dim)`       | Constructs a Matrix instance with the specified difficulty level and dimension. |

## Methods

| Signature                           | Description                                       |
| ----------------------------------- | ------------------------------------------------- |
| `void placeMines()`                 | Places mines randomly in the matrix.              |
| `void displayMatrix()`              | Displays the matrix.                              |
| `int findMinesAround(int i, int j)` | Finds the number of mines around a specific case. |
| `void displayMinesAround()`         | Displays the number of mines around each case.    |
| `int getDim()`                      | Returns the dimension of the matrix.              |
| `boolean getCase(int i, int j)`     | Returns the value of a specific case.             |
| `int getNoMines()`                  | Returns the number of mines in the matrix.        |
| `void setDim(int dim)`              | Sets the dimension of the matrix.                 |
| `void setNoMines(int n)`            | Sets the number of mines in the matrix.           |
| `void updateCases()`                | Updates the matrix of cases.                      |

# Case

Represents a cell in the Minesweeper game.

## Fields

| Type               | Field Name    | Description                                                      |
| ------------------ | ------------- | ---------------------------------------------------------------- |
| `String`           | `value`       | Indicates the value in the cell.                                 |
| `final static int` | `DIM`         | Dimension of the cell.                                           |
| `GUI`              | `gui`         | The graphical user interface of the game.                        |
| `int`              | `state`       | The state of the cell (0 = not opened, 1 = opened, 2 = flagged). |
| `int`              | `X`           | X-coordinate of the cell.                                        |
| `int`              | `Y`           | Y-coordinate of the cell.                                        |
| `Color`            | `color`       | The color of the cell.                                           |
| `boolean`          | `isClickable` | Indicates whether the cell is clickable.                         |

## Methods

| Signature                                              | Description                                                            |
| ------------------------------------------------------ | ---------------------------------------------------------------------- |
| `void paintComponent(Graphics g)`                      | Draws the cell, including images for different states.                 |
| `void mouseClicked(MouseEvent e)`                      | Handles mouse click events, changing the cell state accordingly.       |
| `void mousePressed(MouseEvent e)`                      | Handles mouse press events.                                            |
| `void mouseReleased(MouseEvent e)`                     | Handles mouse release events.                                          |
| `void mouseEntered(MouseEvent e)`                      | Handles mouse enter events.                                            |
| `void mouseExited(MouseEvent e)`                       | Handles mouse exit events.                                             |
| `void openCase()`                                      | Opens the cell.                                                        |
| `int getState()`                                       | Returns the current state of the cell.                                 |
| `String getValue()`                                    | Returns the value of the cell.                                         |
| `void setClickable(boolean flag)`                      | Sets whether the cell is clickable or not.                             |
| `void openCaseMultiplayer(String value, String color)` | Opens the cell in multiplayer mode with the specified value and color. |

## Level

Enum for difficulty levels.

## GUI

Handles the game interface.

### Fields

+------------------------+---------------------+------------------------------------------------------------+
| Type | Field Name | Description |
+------------------------+---------------------+------------------------------------------------------------+
| int | nbMinesTrouvees | Number of mines found |
| int | seconds | Seconds on the timer |
| int | minutes | Minutes on the timer |
| int | casesOpened | Number of cases opened |
| Timer | timer | Timer instance for tracking time |
| Matrix | mineMatrix | Matrix instance for mines |
| Main | main | Main instance |
| Level | level | Difficulty level of the game |
| Map<Level, Integer> | levelScores | Map to store scores for each difficulty level |
| Case[][] | tabCases | Array of Case instances representing the game grid |
| Thread | levelSaveThread | Thread for saving the current level |
| boolean | isFirstTest | Flag to indicate if it's the first test for cascade reveal |
| boolean | isMultiplayer | Flag to indicate multiplayer mode |
+------------------------+---------------------+------------------------------------------------------------+

### Methods:

| Signature                                                        | Description                                      |
| ---------------------------------------------------------------- | ------------------------------------------------ |
| `GUI(Main main)`                                                 | Constructor for the GUI class.                   |
| `void openCase(int x, int y, String value, String color)`        | Opens a case in multiplayer mode.                |
| `public void incrementCasesOpened()`                             | Increments the count of opened cases.            |
| `public void decrementCasesOpened()`                             | Decrements the count of opened cases.            |
| `public void disconnect()`                                       | Disconnects from the server.                     |
| `public void GameOverMultiplayer(String username, String score)` | Handles the end of the game in multiplayer mode. |

## Main

Manages a client for the game.

### Fields

| Type                       | Field Name      | Description                               |
| -------------------------- | --------------- | ----------------------------------------- |
| `private GUI`              | `panel`         | Reference to the GUI panel.               |
| `private DataOutputStream` | `out`           | Output stream to the server.              |
| `private DataInputStream`  | `in`            | Input stream from the server.             |
| `private Socket`           | `cSock`         | Socket for communication with the server. |
| `private Thread`           | `userThread`    | Thread for client-server communication.   |
| `private int`              | `portNo`        | Port number for server connection.        |
| `private String`           | `port`          | String representation of the port number. |
| `private String`           | `name`          | Name of the client player.                |
| `private boolean`          | `isGameStarted` | Indicates if the game has started.        |

### Methods

| Signature                            | Description                                            |
| ------------------------------------ | ------------------------------------------------------ |
| `public void JoinServer()`           | Connects the client to a server.                       |
| `private void connectToServer()`     | Connects the client to the specified server.           |
| `void createServer()`                | Creates a server for the client to connect to.         |
| `void sendCoordinates(int x, int y)` | Sends coordinates (x, y) to the server.                |
| `@Override public void run()`        | Runs the client thread to communicate with the server. |
| `void disconnect()`                  | Disconnects the client from the server.                |

## Server

Manages the game server.

### Fields

| Type                | Field Name       | Description                                         |
| ------------------- | ---------------- | --------------------------------------------------- |
| `ArrayList<Socket>` | `userSockets`    | List of sockets representing connected clients      |
| `ArrayList<Thread>` | `clientHandlers` | List of threads representing client handlers        |
| `ServerSocket`      | `gestSock`       | Server socket for handling client connections       |
| `ServerGUI`         | `panel`          | Instance of the `ServerGUI` class                   |
| `Level`             | `level`          | Difficulty level of the game                        |
| `Matrix`            | `matrix`         | Matrix representing the game grid                   |
| `int`               | `nbOpenedCases`  | Number of opened cases in the game                  |
| `boolean`           | `isGameStarted`  | Flag indicating whether the game has started        |
| `Thread`            | `serverThread`   | Thread for managing the server's main functionality |
| `int`               | `dim`            | Dimension of the custom grid in the game            |

### Methods:

| Signature                                                           | Description                                                       |
| ------------------------------------------------------------------- | ----------------------------------------------------------------- |
| `public static void main(String args[])`                            | Main method to instantiate and start the `Server` class           |
| `public void manageServerquit()`                                    | Manages the server shutdown and broadcasts disconnect to clients  |
| `public void startServer(int port)`                                 | Starts the server on the specified port                           |
| `public void closeSocket()`                                         | Closes the server socket and interrupts the server thread         |
| `@Override public void run()`                                       | Runs the server thread                                            |
| `public void setDifficulty(Level l, boolean flag)`                  | Sets the difficulty level of the game                             |
| `public void refreshGrid(String username, String color, int score)` | Updates the server grid with the given client's information       |
| `public void updateNbOpenedCases(String username, int score)`       | Updates the number of opened cases and checks for game completion |
| `public void remove(int index, String username)`                    | Removes a client from the server                                  |
| `public void startGame()`                                           | Starts the game, setting the difficulty and starting the timer    |
| `public boolean getGameStarted()`                                   | Returns the `isGameStarted` flag                                  |

## ServerGUI

Manages the server interface.

### Fields

| Type                 | Field Name  | Description                                             |
| -------------------- | ----------- | ------------------------------------------------------- |
| `Server`             | `server`    | Reference to the associated `Server` instance           |
| `Timer`              | `timer`     | Timer for updating the displayed time in the server GUI |
| `int`                | `seconds`   | Count of seconds for the timer                          |
| `int`                | `minutes`   | Count of minutes for the timer                          |
| `ArrayList<String>`  | `usernames` | List of usernames displayed in the server grid          |
| `ArrayList<String>`  | `colors`    | List of colors associated with usernames                |
| `ArrayList<Integer>` | `scores`    | List of scores associated with usernames                |

### Methods:

| Signature                                                           | Description                                              |
| ------------------------------------------------------------------- | -------------------------------------------------------- |
| `private void initMenuBar()`                                        | Initializes the menu bar and its items                   |
| `private void initButtons()`                                        | Initializes the buttons in the server GUI                |
| `private void initGrid()`                                           | Initializes the grid for displaying usernames and scores |
| `public void refreshGrid(String username, String color, int score)` | Refreshes the grid with the given user information       |
| `private void reloadGrid()`                                         | Reloads the grid with the updated user information       |
| `public void removeUsername(int index, String username)`            | Removes a username from the grid                         |
| `void initTimer()`                                                  | Initializes the timer in the server GUI                  |
| `void resetTimer()`                                                 | Resets the timer to zero                                 |
| `void stopTimer()`                                                  | Stops the timer                                          |
| `void startTimer()`                                                 | Starts the timer                                         |
| `public void initScores()`                                          | Initializes the scores to zero and reloads the grid      |

## ClientHandler

Handles a connected client to the server.

### Fields:

| Type               | Field Name     | Description                                    |
| ------------------ | -------------- | ---------------------------------------------- |
| `Socket`           | `clientSocket` | Socket representing the client connection      |
| `DataInputStream`  | `in`           | Input stream for reading data from the client  |
| `DataOutputStream` | `out`          | Output stream for sending data to the client   |
| `Matrix`           | `matrix`       | Matrix instance for handling game data         |
| `Server`           | `server`       | Reference to the associated `Server` instance  |
| `String`           | `username`     | Username of the client                         |
| `String`           | `color`        | Color associated with the client               |
| `int`              | `score`        | Score of the client                            |
| `Thread`           | `clientThread` | Thread for handling the client's communication |

### Methods:

| Signature                                      | Description                                                                                    |
| ---------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| `public void run()`                            | Overrides the `run` method of the `Runnable` interface, handles communication with the client. |
| `public static void broadCast(String message)` | Sends a message to all connected clients                                                       |
| `public void updateMatrix(Matrix matrix)`      | Updates the matrix instance for the client                                                     |
| `public String getUsername()`                  | Returns the username of the client                                                             |
| `public String getColor()`                     | Returns the color associated with the client                                                   |
| `public int getScore()`                        | Returns the score of the client                                                                |

## How to Run

1. Ensure you have Java installed on your system.
2. Compile the Java files.
   ```bash
   javac JK/*.java
   ```
