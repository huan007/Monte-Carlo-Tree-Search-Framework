package GomokuExample;

import MCTS.GameState;
import MCTS.MCTS;
import MCTS.Move;
import MCTS.Player;

import java.util.*;

public class GomokuGameState extends GameState {
    int[][] m_boardValues;
    int m_boardSize;
    GomokuMove<Point> m_lastMove;

    public final static Player BLACK_PLAYER = new Player("b", 11);
    public final static Player WHITE_PLAYER = new Player("w", 88);
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
        this.m_lastMove = oldState.m_lastMove;
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
        m_lastMove = (GomokuMove<Point>) move;
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
        if (m_lastMove == null)
            return 0;
        Point lastMoveCoordinate = (Point) m_lastMove.getMoveValue();
        int originalX = lastMoveCoordinate.x;
        int originalY = lastMoveCoordinate.y;
        int countN = getConsecutiveCount(playerValue, originalX, originalY, 0, 1);
        int countS = getConsecutiveCount(playerValue, originalX, originalY, 0, -1);
        int countE = getConsecutiveCount(playerValue, originalX, originalY, 1, 0);
        int countW = getConsecutiveCount(playerValue, originalX, originalY, -1, 0);
        int countSE = getConsecutiveCount(playerValue, originalX, originalY, 1, 1);
        int countNW = getConsecutiveCount(playerValue, originalX, originalY, -1, -1);
        int countNE = getConsecutiveCount(playerValue, originalX, originalY, 1, -1);
        int countSW = getConsecutiveCount(playerValue, originalX, originalY, -1, 1);
        if ((countN + countS + 1) >= 5)
            return playerValue;
        if ((countE + countW + 1) >= 5)
            return playerValue;
        if ((countSE + countNW + 1) >= 5)
            return playerValue;
        if ((countNE + countSW + 1) >= 5)
            return playerValue;
        return 0;
    }

    private int getConsecutiveCount(int playerValue, int originalX, int originalY, int offsetX, int offsetY) {
        int consecutiveCount = 0;
        int x = originalX;
        int y = originalY;
        while (true) {
            x += offsetX;
            y += offsetY;
            if ((x < 0) || (x >= m_boardSize) || (y < 0) || (y >= m_boardSize))
                break;
            if (m_boardValues[x][y] == playerValue)
                consecutiveCount++;
            else
                break;
        }
        return consecutiveCount;
    }

    @Override
    protected void generateNextPossibleMoves() {
        m_possibleMoves.clear();
        Random random = new Random();
        List<Point> myPieces = new ArrayList<>();
        List<Point> otherPlayerPieces = new ArrayList<>();
        // Use a Set for move so there will not be duplicated moves
        HashMap<String, GomokuMove> moveSet = new HashMap<>();
        int myValue = m_nextPlayer.getId();
        int otherPlayerValue = m_player.getId();
        for (int x = 0; x < m_boardSize; x++) {
            for (int y = 0; y < m_boardSize; y++) {
                int value = m_boardValues[x][y];
                if (value == myValue)
                    myPieces.add(new Point(x,y));
                else if (value == otherPlayerValue)
                    otherPlayerPieces.add(new Point(x,y));
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
                        // Only put in moves that would create 3 or more in a rows for myself
                        int countN = getConsecutiveCount(myValue, newX, newY, 0, 1);
                        int countS = getConsecutiveCount(myValue, newX, newY, 0, -1);
                        int countE = getConsecutiveCount(myValue, newX, newY, 1, 0);
                        int countW = getConsecutiveCount(myValue, newX, newY, -1, 0);
                        int countSE = getConsecutiveCount(myValue, newX, newY, 1, 1);
                        int countNW = getConsecutiveCount(myValue, newX, newY, -1, -1);
                        int countNE = getConsecutiveCount(myValue, newX, newY, 1, -1);
                        int countSW = getConsecutiveCount(myValue, newX, newY, -1, 1);
                        if ((countN + countS) >= 4)
                            moveSet.put(newMove.toString(), newMove);
                        else if ((countE + countW) >= 4)
                            moveSet.put(newMove.toString(), newMove);
                        else if ((countSE + countNW) >= 4)
                            moveSet.put(newMove.toString(), newMove);
                        else if ((countNE + countSW) >= 4)
                            moveSet.put(newMove.toString(), newMove);
                    }
                }
            }
        }
        // Adding possible moves that deter the opponent
        for (Point piece : otherPlayerPieces) {
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
