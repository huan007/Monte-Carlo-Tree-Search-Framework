package Game2048Example;

import MCTS.Move;

public class Game2048Move extends Move<Integer> {
    public Game2048Move(String moveName, Integer moveValue) {
        super(moveName, moveValue);
    }

    @Override
    public String toString() {
        return m_moveName;
    }
}
