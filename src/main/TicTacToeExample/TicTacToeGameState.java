package TicTacToeExample;

import MCTS.GameState;
import MCTS.Move;
import MCTS.Player;

public class TicTacToeGameState extends GameState {
    int[][] m_boardValues;
    int m_boardSize;

    public TicTacToeGameState(Player player, int[][] boardValues, int boardSize) {
        super(player);
        m_boardValues = boardValues;
        m_boardSize = boardSize;
        generateNextPossibleMoves();
    }

    public TicTacToeGameState(TicTacToeGameState oldState) {
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
        TicTacToeGameState newGameState = new TicTacToeGameState(this);
        return newGameState;
    }

    @Override
    protected void makeMove(Move move) {
        Point point = (Point) move.getMoveValue();
        m_boardValues[point.x][point.y] = m_nextPlayer.getId();
    }

    @Override
    protected Player determineNextPlayer() {
        if (m_player.equals(TicTacToe.playerX))
            return TicTacToe.playerO;
        return TicTacToe.playerX;
    }

    @Override
    protected void determineTerminalAndWinner() {
        // Check if terminal
        m_isTerminal = true;
        for (int x = 0; x < m_boardSize; x++) {
            for (int y = 0; y < m_boardSize; y++) {
                if (m_boardValues[x][y] == 0)
                    m_isTerminal = false;
            }
        }
        int value = m_player.getId();
        // Check for consecutive vertical pieces
        for (int x = 0; x < m_boardSize; x++) {
            int consecutiveCount = 0;
            for (int y = 0; y < m_boardSize; y++) {
                if (m_boardValues[x][y] == value)
                    consecutiveCount++;
                else
                    break;

            }
            if (consecutiveCount == m_boardSize) {
                m_winner = m_player;
                m_isTerminal = true;
                return;
            }
        }
        // Check for consecutive horizontal pieces
        for (int y = 0; y < m_boardSize; y++) {
            int consecutiveCount = 0;
            for (int x = 0; x < m_boardSize; x++) {
                if (m_boardValues[x][y] == value)
                    consecutiveCount++;
                else
                    break;

            }
            if (consecutiveCount == m_boardSize) {
                m_winner = m_player;
                m_isTerminal = true;
                return;
            }
        }
        // Check for diagonal pieces
        int x = 0;
        int y = 0;
        int consecutiveCount = 0;
        for (int i = 0; i < m_boardSize; i++) {
            if (m_boardValues[x][y] == value) {
                consecutiveCount++;
                x++;
                y++;
            }
            else
                break;
        }
        if (consecutiveCount == m_boardSize) {
            m_winner = m_player;
            m_isTerminal = true;
            return;
        }

        x = m_boardSize-1;
        y = 0;
        consecutiveCount = 0;
        for (int i = 0; i < m_boardSize; i++) {
            if (m_boardValues[x][y] == value) {
                consecutiveCount++;
                x--;
                y++;
            }
            else
                break;
        }
        if (consecutiveCount == m_boardSize) {
            m_winner = m_player;
            m_isTerminal = true;
            return;
        }
    }

    @Override
    protected void generateNextPossibleMoves() {
        m_possibleMoves.clear();
        for (int x = 0; x < m_boardSize; x++) {
            for (int y = 0; y < m_boardSize; y++) {
                if (m_boardValues[x][y] == 0) {
                    Point point = new Point(x,y);
                    m_possibleMoves.add(new TicTacToeMove(point.toString(), point));
                }
            }
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
        for (int i = 0; i < m_boardSize; i++) {
            for (int j = 0; j < m_boardSize; j++) {
                result += m_boardValues[i][j];
                result += "\t";
            }
            result += "\n";
        }
        return result;
    }
}