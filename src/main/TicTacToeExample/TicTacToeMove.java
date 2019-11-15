package TicTacToeExample;

public class TicTacToeMove<Point> extends MCTS_Framework.Move {
    public TicTacToeMove(String moveName, Point moveValue) {
        super(moveName, moveValue);
    }

    @Override
    public String toString() {
        return ((Point) m_moveValue).toString();
    }
}
