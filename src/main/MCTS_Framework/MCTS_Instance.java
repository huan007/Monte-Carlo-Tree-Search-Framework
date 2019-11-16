package MCTS_Framework;

public class MCTS_Instance implements Runnable {
    MCTS m_mcts;

    public MCTS_Instance(MCTS mcts) {
        m_mcts = mcts;
    }

    @Override
    public void run() {
        m_mcts.uct_search();
    }
}
