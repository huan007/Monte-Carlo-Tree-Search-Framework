package MCTS;

public class Move<T> {
    private String m_moveName;
    private T m_moveValue;
    public Move(String moveName, T moveValue) {
        m_moveName = moveName;
        m_moveValue = moveValue;
    }

    public String getM_moveName() {
        return m_moveName;
    }

    public T getM_moveValue() {
        return m_moveValue;
    }
}