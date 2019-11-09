package Game2048Example;

import MCTS.GameState;
import MCTS.Move;
import MCTS.Player;

import java.util.Random;

public class GameState2048 extends GameState {
    int[][] m_boardValues;
    int m_boardSize;
    int m_score;
    int m_rootLargestValue;

    public final static Player player = new Player("Player", 1);
    public final static Player playerAI = new Player("AI", 2);

    public GameState2048(Player player, int[][] values, int boardSize, int score, int rootLargestValue) {
        super(player);
        m_boardValues = values;
        m_boardSize = boardSize;
        m_score = score;
        m_rootLargestValue = rootLargestValue;
        generateNextPossibleMoves();
    }

    public GameState2048(GameState2048 oldState) {
        super(oldState);
        this.m_boardSize = oldState.m_boardSize;
        this.m_boardValues = new int[m_boardSize][m_boardSize];
        // Perform deep copy of 2D array
        for (int i = 0; i < m_boardSize; i++)
            for (int j = 0; j < m_boardSize; j++)
                this.m_boardValues[i][j] = oldState.m_boardValues[i][j];
    }

    @Override
    public GameState2048 deepCopy() {
        return new GameState2048(this);
    }

    @Override
    protected void makeMove(Move move) {
        moveBoard((Integer) move.getMoveValue(), m_boardValues);
    }

    @Override
    protected Player determineNextPlayer() {
        return player;
    }

    @Override
    protected boolean determineTerminal() {
        return !checkIfCanGo(m_boardValues);
    }

    @Override
    protected Player determineWinner() {
        int largestValue = 0;
        for (int x = 0; x < m_boardSize; x++) {
            for (int y = 0; y < m_boardSize; y++) {
                if (m_boardValues[x][y] > largestValue)
                    largestValue = m_boardValues[x][y];
            }
        }
        // Only "win" if we made impressive progress
        if (largestValue >= 13)
            return player;
        else return null;
    }

    @Override
    protected void generateNextPossibleMoves() {
        m_possibleMoves.clear();
        for (Directions direction : Directions.values()) {
            if (checkIfCanMoveDirection(direction, m_boardValues))
                m_possibleMoves.add(new Move(direction.name(), direction.getRotateValue()));
        }
    }

    @Override
    protected boolean isSuccessful(Player winner) {
        if (winner == null)
            return false;
        return isWinner(winner);
    }

    private void moveBoard(int direction, int[][] boardValues) {
        for (int i = 0; i < direction; i++) {
            this.rotateMatrixClockwise(boardValues);
        }
        if (this.canMove(boardValues)) {
            this.moveTiles(boardValues);
            this.mergeTiles(boardValues);
            placeRandomTile(boardValues, m_boardSize);
        }
        for (int i = 0; i < ((4 - direction) % 4); i++) {
            this.rotateMatrixClockwise(boardValues);
        }
    }

    private boolean canMove(int[][] m_boardValues) {
        int[][] board = m_boardValues;
        int boardSize = this.m_boardSize;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 1; j < boardSize; j++) {
                if (board[i][j-1] == 0 && board[i][j] > 0)
                    return true;
                else if ((board[i][j-1] == board[i][j]) && (board[i][j-1] != 0))
                    return true;
            }
        }
        return false;
    }

    private void rotateMatrixClockwise(int[][] boardValues) {
        int[][] board = boardValues;
        int boardSize = this.m_boardSize;
        int halfLimit = (int) (boardSize / 2);
        for (int i = 0; i < halfLimit; i++) {
            for (int j = i; j < boardSize - i - 1; j++) {
                int temp1 = board[i][j];
                int temp2 = board[boardSize - 1 - j][i];
                int temp3 = board[boardSize - 1 - i][boardSize - 1 - j];
                int temp4 = board[j][boardSize - 1 - i];
                board[boardSize - 1 - j][i] = temp1;
                board[boardSize - 1 - i][boardSize - 1 - j] = temp2;
                board[j][boardSize - 1 - i] = temp3;
                board[i][j] = temp4;
            }
        }
    }

    private void moveTiles(int[][] boardValues) {
        int[][] tm = boardValues;
        int boardSize = this.m_boardSize;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize - 1; j++) {
                while ((tm[i][j] == 0) && (sumList(tm[i], j) > 0)) {
                    for (int k = j; k < boardSize - 1; k++) {
                        tm[i][k] = tm[i][k + 1];
                    }
                    tm[i][boardSize - 1] = 0;
                }
            }
        }
    }

    private void mergeTiles(int[][] boardValues) {
        int[][] tm = boardValues;
        int boardSize = this.m_boardSize;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize - 1; j++) {
                if ((tm[i][j] == tm[i][j + 1]) && (tm[i][j] != 0)) {
                    tm[i][j] = tm[i][j] + 1;
                    tm[i][j + 1] = 0;
                    this.m_score += tm[i][j];
                    this.moveTiles(tm);
                }
            }
        }
    }

    public static void placeRandomTile(int[][] boardValues, int boardSize) {
        int i = 0;
        int j = 0;
        Random random = new Random();
        while (true) {
            i = random.nextInt(boardSize);
            j = random.nextInt(boardSize);
            if (boardValues[i][j] == 0)
                break;
        }
        if (random.nextInt(100) < 90)
            boardValues[i][j] = 1;
        else
            boardValues[i][j] = 2;
    }

    private boolean checkIfCanGo(int[][] boardValues) {
        int range = m_boardSize * m_boardSize;
        for (int i = 0; i < range; i++) {
            if (boardValues[(i / m_boardSize)][i % m_boardSize] == 0)
                return true;
        }
        for (int i = 0; i < m_boardSize; i++) {
            for (int j = 0; j < m_boardSize - 1; j++) {
                if (boardValues[i][j] == boardValues[i][j + 1])
                    return true;
                else if (boardValues[j][i] == boardValues[j + 1][i])
                    return true;
            }
        }
        return false;
    }

    private boolean checkIfCanMoveDirection(Directions direction, int[][] boardValues) {
        boolean result = false;
        for (int i = 0; i < direction.getRotateValue(); i++) {
            this.rotateMatrixClockwise(boardValues);
        }
        if (this.canMove(boardValues)) {
            result = true;
        }
        for (int i = 0; i < ((4 - direction.getRotateValue()) % 4); i++) {
            this.rotateMatrixClockwise(boardValues);
        }
        return result;
    }

    private int sumList(int[] list, int start_index) {
        int sum = 0;
        for (int i = start_index; i < list.length; i++) {
            sum += list[i];
        }
        return sum;
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

    public enum Directions {
        UP(1), DOWN(3), LEFT(0), RIGHT(2);
        private final int rotateValue;

        Directions(int rotateValue) {
            this.rotateValue = rotateValue;
        }

        int getRotateValue() {
            return rotateValue;
        }

        public static Directions getRandomDirection() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
