package MCTS_Framework;

public class Move<T> {
    protected String m_moveName;
    protected T m_moveValue;
    public Move(String moveName, T moveValue) {
        m_moveName = moveName;
        m_moveValue = moveValue;
    }

    public String getMoveName() {
        return m_moveName;
    }

    public T getMoveValue() {
        return m_moveValue;
    }
}