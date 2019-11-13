package GomokuExample;

import MCTS.GameState;
import MCTS.Move;
import MCTS.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GomokuGameState extends GameState {
    int[][] m_boardValues;
    int m_boardSize;

    public final static Player BLACK_PLAYER = new Player("b", 1);
    public final static Player WHITE_PLAYER = new Player("w", 2);
    public final static int MINIMUM_CONSECUTIVE_PIECES = 5;

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
        // Boundaries
        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;
        // List of empty spaces
        List<Point> emptySpaces = new ArrayList<>();
        for (int x = 0; x < m_boardSize; x++) {
            for (int y = 0; y < m_boardSize; y++) {
                int value = m_boardValues[x][y];
                // Empty Spaces
                if (value == 0)
                    emptySpaces.add(new Point(x,y));
                // Non empty
                else {
                    if (minX == 0)
                        minX = x;
                    if (minY == 0)
                        minY = y;
                    if (x > maxX)
                        maxX = x;
                    if (y > maxY)
                        maxY = y;
                }
            }
        }
        // If we are just starting out, then make a small play area in the middle
        if ((maxX == 0) && (maxY == 0)) {
            minX = (m_boardSize / 2) - 2;
            minY = (m_boardSize / 2) - 2;
            maxX = (m_boardSize / 2) + 2;
            maxY = (m_boardSize / 2) + 2;
        }

        for (Point emptySpace : emptySpaces) {
            int x = emptySpace.x;
            int y = emptySpace.y;
            if ((x >= minX-1) && (x <= maxX+1) && (y >= minY-1) && (y <= maxY+1))
                m_possibleMoves.add(new GomokuMove(emptySpace.toString(), emptySpace));
            // If empty space is not in bound, then there is only a slight chance of being added to the list (exploration)
            else if (random.nextInt(100) < 1)
                m_possibleMoves.add(new GomokuMove(emptySpace.toString(), emptySpace));
            else if (m_possibleMoves.size() == 0)
                m_possibleMoves.add(new GomokuMove(emptySpace.toString(), emptySpace));
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
