package MCTS;

public class SimulationResult {
    Player m_winner;
    float m_rewardValue;

    public SimulationResult(Player m_winner, float m_rewardValue) {
        this.m_winner = m_winner;
        this.m_rewardValue = m_rewardValue;
    }

    public Player getWinner() {
        return m_winner;
    }

    public float getRewardValue() {
        return m_rewardValue;
    }
}
