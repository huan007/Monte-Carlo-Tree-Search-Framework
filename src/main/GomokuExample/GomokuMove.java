package GomokuExample;

public class GomokuMove<Point> extends MCTS.Move {
    public GomokuMove(String moveName, Point moveValue) {
        super(moveName, moveValue);
    }

    @Override
    public String toString() {
        return (m_moveValue).toString();
    }
}
