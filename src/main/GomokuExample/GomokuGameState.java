package GomokuExample;

import MCTS.GameState;
import MCTS.Move;
import MCTS.Player;

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
            for (int y = 0; y < m_boardSize; y++) {
                if (m_boardValues[x][y] == playerValue)
                    consecutiveCount++;
                // If not then reset counter
                else
                    consecutiveCount = 0;
                if (consecutiveCount >= MINIMUM_CONSECUTIVE_PIECES)
                    return playerValue;
            }

        }
        // Check for consecutive horizontal pieces
        for (int y = 0; y < m_boardSize; y++) {
            int consecutiveCount = 0;
            for (int x = 0; x < m_boardSize; x++) {
                if (m_boardValues[x][y] == playerValue)
                    consecutiveCount++;
                else
                    consecutiveCount = 0;
                if (consecutiveCount >= MINIMUM_CONSECUTIVE_PIECES)
                    return playerValue;
            }
        }
        // Check for diagonal pieces with -1 slope
        for (int startY = m_boardSize - 1; startY >= 0; startY--) {
            int x = 0;
            int consecutiveCount1 = 0;
            int consecutiveCount2 = 0;
            for (int y = startY; y < m_boardSize; y++, x++) {
                // Check bottom half of the board
                if (m_boardValues[x][y] == playerValue)
                    consecutiveCount1++;
                else
                    consecutiveCount1 = 0;

                // Check top half of the board
                if (m_boardValues[y][x] == playerValue)
                    consecutiveCount2++;
                else
                    consecutiveCount2 = 0;
                if (consecutiveCount1 >= MINIMUM_CONSECUTIVE_PIECES)
                    return playerValue;
                if (consecutiveCount2 >= MINIMUM_CONSECUTIVE_PIECES)
                    return playerValue;
            }
        }
        // Check for diagonal pieces with +1 slope
        for (int startY = 0; startY < m_boardSize; startY++) {
            int x = 0;
            int consecutiveCount1 = 0;
            int consecutiveCount2 = 0;
            for (int y = startY; y >= 0; y--, x++) {
                // Check bottom half of the board
                if (m_boardValues[x][y] == playerValue)
                    consecutiveCount1++;
                else
                    consecutiveCount1 = 0;

                // Check top half of the board
                if (m_boardValues[m_boardSize-1-y][m_boardSize-1-x] == playerValue)
                    consecutiveCount2++;
                else
                    consecutiveCount2 = 0;
                if (consecutiveCount1 >= MINIMUM_CONSECUTIVE_PIECES)
                    return playerValue;
                if (consecutiveCount2 >= MINIMUM_CONSECUTIVE_PIECES)
                    return playerValue;
            }
        }
        return 0;
    }

    @Override
    protected void generateNextPossibleMoves() {
        // TODO:
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
