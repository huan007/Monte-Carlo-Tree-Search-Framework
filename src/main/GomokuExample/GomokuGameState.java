package GomokuExample;

import MCTS.GameState;
import MCTS.MCTS;
import MCTS.Move;
import MCTS.Player;

import java.util.*;

public class GomokuGameState extends GameState {
    int[][] m_boardValues;
    int m_boardSize;

    public final static Player BLACK_PLAYER = new Player("b", 1);
    public final static Player WHITE_PLAYER = new Player("w", 2);
    public final static int MINIMUM_CONSECUTIVE_PIECES = 5;
    public final static Point[] DIRECTIONS8 = new Point[] {
            new Point(1,0), new Point(0,1), new Point(-1,0), new Point(0,-1),
            new Point(1, 1), new Point(-1, 1), new Point(-1,-1), new Point(1,-1)
    };

    public GomokuGameState(Player player, int[][] boardValues) {
        super(player);
        m_boardValues = boardValues;
        m_boardSize = 19;
        generateNextPossibleMoves();
    }

    public GomokuGameState(GomokuGameState oldState) {
        super(oldState);
        this.m_boardSize = oldState.m_boardSize;
        this.m_boardValues = new int[m_boardSize][m_boardSize];
        // Perform deep copy of 2D array
        for (int i = 0; i < m_boardSize; i++)
            for (int j = 0; j < m_boardSize; j++)
                this.m_boardValues[i][j] = oldState.m_boardValues[i][j];
    }

    @Override
    public GameState deepCopy() {
        GomokuGameState newGameState = new GomokuGameState(this);
        return newGameState;
    }

    @Override
    protected void makeMove(Move move) {
        Point point = (Point) move.getMoveValue();
        m_boardValues[point.x][point.y] = m_nextPlayer.getId();
    }

    @Override
    protected Player determineNextPlayer() {
        if (m_player.equals(BLACK_PLAYER))
            return WHITE_PLAYER;
        else return BLACK_PLAYER;
    }

    @Override
    protected boolean determineTerminal() {
        // Assume terminal by default
        boolean isTerminal = true;
        // If there is still room for another move
        for (int x = 0; x < m_boardSize; x++) {
            for (int y = 0; y < m_boardSize; y++) {
                if (m_boardValues[x][y] == 0)
                    isTerminal = false;
            }
        }
        // If the player that just went won return true
        int winner = determineIfPlayerWin(m_player.getId());
        if (winner == m_player.getId()) {
            m_winner = m_player;
            return true;
        }
        else return isTerminal;
    }

    @Override
    protected Player determineWinner() {
        // Winner has already been determined by isTerminal()
        return m_winner;
    }

    private int determineIfPlayerWin(int playerValue) {
        // Check for consecutive vertical pieces
        for (int x = 0; x < m_boardSize; x++) {
            int consecutiveCount = 0;
            List<Point> consecutivePieces = new ArrayList<>();
            for (int y = 0; y < m_boardSize; y++) {
                if (m_boardValues[x][y] == playerValue) {
                    consecutiveCount++;
                    consecutivePieces.add(new Point(x,y));
                }
                // If not then reset counter
                else {
                    consecutiveCount = 0;
                    consecutivePieces.clear();
                }
                if (consecutiveCount >= MINIMUM_CONSECUTIVE_PIECES) {
                    return playerValue;
                }
            }

        }
        // Check for consecutive horizontal pieces
        for (int y = 0; y < m_boardSize; y++) {
            int consecutiveCount = 0;
            List<Point> consecutivePieces = new ArrayList<>();
            for (int x = 0; x < m_boardSize; x++) {
                if (m_boardValues[x][y] == playerValue) {
                    consecutiveCount++;
                    consecutivePieces.add(new Point(x,y));
                }
                else {
                    consecutiveCount = 0;
                    consecutivePieces.clear();
                }
                if (consecutiveCount >= MINIMUM_CONSECUTIVE_PIECES) {
                    return playerValue;
                }
            }
        }
        // Check for diagonal pieces with -1 slope
        for (int startY = m_boardSize - 1; startY >= 0; startY--) {
            int x = 0;
            int consecutiveCount1 = 0;
            int consecutiveCount2 = 0;
            List<Point> consecutivePieces1 = new ArrayList<>();
            List<Point> consecutivePieces2 = new ArrayList<>();
            for (int y = startY; y < m_boardSize; y++, x++) {
                // Check bottom half of the board
                if (m_boardValues[x][y] == playerValue) {
                    consecutiveCount1++;
                    consecutivePieces1.add(new Point(x,y));
                }
                else {
                    consecutiveCount1 = 0;
                    consecutivePieces1.clear();
                }

                // Check top half of the board
                if (m_boardValues[y][x] == playerValue) {
                    consecutiveCount2++;
                    consecutivePieces2.add(new Point(x,y));
                }
                else {
                    consecutiveCount2 = 0;
                    consecutivePieces2.clear();
                }

                if (consecutiveCount1 >= MINIMUM_CONSECUTIVE_PIECES) {
                    return playerValue;
                }
                if (consecutiveCount2 >= MINIMUM_CONSECUTIVE_PIECES) {
                    return playerValue;
                }
            }
        }
        // Check for diagonal pieces with +1 slope
        for (int startY = 0; startY < m_boardSize; startY++) {
            int x = 0;
            int consecutiveCount1 = 0;
            int consecutiveCount2 = 0;
            List<Point> consecutivePieces1 = new ArrayList<>();
            List<Point> consecutivePieces2 = new ArrayList<>();
            for (int y = startY; y >= 0; y--, x++) {
                // Check bottom half of the board
                if (m_boardValues[x][y] == playerValue) {
                    consecutiveCount1++;
                    consecutivePieces1.add(new Point(x,y));
                }
                else {
                    consecutiveCount1 = 0;
                    consecutivePieces1.clear();
                }

                // Check top half of the board
                if (m_boardValues[m_boardSize-1-y][m_boardSize-1-x] == playerValue) {
                    consecutiveCount2++;
                    consecutivePieces2.add(new Point(x,y));
                }
                else {
                    consecutiveCount2 = 0;
                    consecutivePieces2.clear();
                }

                if (consecutiveCount1 >= MINIMUM_CONSECUTIVE_PIECES) {
                    return playerValue;
                }
                if (consecutiveCount2 >= MINIMUM_CONSECUTIVE_PIECES) {
                    return playerValue;
                }
            }
        }
        return 0;
    }

    @Override
    protected void generateNextPossibleMoves() {
        m_possibleMoves.clear();
        Random random = new Random();
        List<Point> myPieces = new ArrayList<>();
        List<Point> nextPlayerPieces = new ArrayList<>();
        // Use a Set for move so there will not be duplicated moves
        HashMap<String, GomokuMove> moveSet = new HashMap<>();
        int myValue = m_player.getId();
        int nextPlayerValue = m_nextPlayer.getId();
        for (int x = 0; x < m_boardSize; x++) {
            for (int y = 0; y < m_boardSize; y++) {
                int value = m_boardValues[x][y];
                if (value == myValue)
                    myPieces.add(new Point(x,y));
                else if (value == nextPlayerValue)
                    nextPlayerPieces.add(new Point(x,y));
            }
        }
        // Adding possible moves that advance myself
        for (Point piece : myPieces) {
            int x = piece.x;
            int y = piece.y;
            // Check for empty spaces around particular pieces
            for (Point direction : DIRECTIONS8) {
                int newX = x + direction.x;
                int newY = y + direction.y;
                // Check to make sure coordinate is not out of bound
                if ((newX > -1) && (newX < m_boardSize) && (newY > -1) && (newY < m_boardSize)) {
                    // If this is an empty space then add to moveSet
                    if (m_boardValues[newX][newY] == 0) {
                        Point newPoint = new Point(newX, newY);
                        GomokuMove newMove = new GomokuMove(newPoint.toString(), newPoint);
                        moveSet.put(newMove.toString(), newMove);
                    }
                }
            }
        }
        // Adding possible moves that deter the opponent
        for (Point piece : nextPlayerPieces) {
            int x = piece.x;
            int y = piece.y;
            // Check for empty spaces around particular pieces
            for (Point direction : DIRECTIONS8) {
                int newX = x + direction.x;
                int newY = y + direction.y;
                // Check to make sure coordinate is not out of bound
                if ((newX > -1) && (newX < m_boardSize) && (newY > -1) && (newY < m_boardSize)) {
                    // If this is an empty space then add to moveSet
                    if (m_boardValues[newX][newY] == 0) {
                        Point newPoint = new Point(newX, newY);
                        GomokuMove newMove = new GomokuMove(newPoint.toString(), newPoint);
                        moveSet.put(newMove.toString(), newMove);
                    }
                }
            }
        }
        // Add unique moves to possible moves
        m_possibleMoves.addAll(moveSet.values());
        if (m_possibleMoves.size() == 0) {
            Point startingPoint = new Point(m_boardSize/2, m_boardSize / 2);
            m_possibleMoves.add(new GomokuMove(startingPoint.toString(), startingPoint));
        }
    }

    @Override
    protected boolean isSuccessful(Player winner) {
        if (winner == null)
            return true;
        return isWinner(winner);
    }

    @Override
    public String toString() {
        String result = "";
        for (int y = 0; y < m_boardSize; y++) {
            for (int x = 0; x < m_boardSize; x++) {
                result += m_boardValues[x][y];
                result += "\t";
            }
            result += "\n";
        }
        return result;
    }
}
