package GomokuExample;

public class GomokuMove<Point> extends MCTS_Framework.Move {
    public GomokuMove(String moveName, Point moveValue) {
        super(moveName, moveValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GomokuMove))
            return false;
        else {
            GomokuMove otherMove = (GomokuMove) obj;
            return m_moveValue.equals(otherMove);
        }
    }

    @Override
    public String toString() {
        return (m_moveValue).toString();
    }
}
