package MCTS_Framework;

import java.util.ArrayList;
import java.util.List;

public class MCTSNode {
    private int m_visitCount;
    private float m_winCount;
    private MCTSNode m_parent;
    private Move m_lastMove;
    private List<MCTSNode> m_children;
    private GameState m_gameState;

    protected MCTSNode(MCTSNode parent) {
        m_visitCount = 0;
        m_winCount = 0;
        m_parent = parent;
        m_lastMove = null;
        m_children = new ArrayList<>();
    }

    public MCTSNode(GameState gameState, MCTSNode parent) {
        m_visitCount = 0;
        m_winCount = 0;
        m_parent = parent;
        m_lastMove = null;
        m_children = new ArrayList<>();
        m_gameState = gameState.deepCopy();
    }

    public List<MCTSNode> getChildren() {
        return m_children;
    }

    public Move getLastMove() {
        return m_lastMove;
    }

    public float getWinCount() {
        return m_winCount;
    }

    public int getVisitCount() {
        return m_visitCount;
    }

    public MCTSNode getParent() {
        return m_parent;
    }

    public GameState getGameState() {
        return m_gameState;
    }

    public void incrementVisitCount() {
        m_visitCount++;
    }

    public void incrementWinCount(float rewardPoint) {
        m_winCount += rewardPoint;
    }

    public void addChild(MCTSNode childMCTSNode) {
        m_children.add(childMCTSNode);
    }

    public MCTSNode createChildNodeWithMove(Move move) {
        GameState nextGameState = m_gameState.deepCopy();
        nextGameState.moveToNextState(move);
        MCTSNode newMCTSNode = new MCTSNode(nextGameState, this);
        newMCTSNode.setLastMove(move);
        return newMCTSNode;
    }

    protected void setWinCount(float winCount) {
        m_winCount = winCount;
    }

    protected void setVisitCount(int visitCount) {
        m_visitCount = visitCount;
    }

    public void setLastMove(Move move) {
        m_lastMove = move;
    }

    public boolean isTerminal() {
        return m_gameState.isTerminal();
    }

    public boolean isFullyExpanded() {
        // If there are not any possible moves left, then node is fully expanded
        return m_gameState.getPossibleMoves().size() == 0;
    }

    @Override
    public String toString() {
        return m_gameState.toString();
    }
}
