package MCTS;

import java.util.List;

public class MCTS {
    private int m_stepSize;
    private float m_factor;
    private MCTSNode m_rootMCTSNode;
    private List<Player> m_possiblePlayers;
    private List<Move> m_possibleMoves;

    public static class Builder {

    }

    private MCTS() {

    }

    public Move uct_search() {
        /*
         *  while within computational budget do
         *      s <- Selection(root)
         *      winner <- Simulation(s)
         *      Propagation(s, winner)
         *  end while
         *  return Action(argmax)
         */
        for (int iteration = 0; iteration < m_stepSize; iteration++) {
            MCTSNode leafMCTSNode = selection(m_rootMCTSNode);
            Player winner = simulation(leafMCTSNode);
            backPropagation(leafMCTSNode, winner);
        }
        float maxValue = -Float.MAX_VALUE;
        int choiceIndex = 0;
        for (int i = 0; i < m_rootMCTSNode.getChildren().size(); i++) {
            MCTSNode child = (MCTSNode) m_rootMCTSNode.getChildren().get(i);
            float value = (float) child.getWinCount() / (float) child.getVisitCount();
            if (value > maxValue) {
                maxValue = value;
                choiceIndex = i;
            }
        }
        return ((MCTSNode) m_rootMCTSNode.getChildren().get(choiceIndex)).getLastMove();
    }

    private MCTSNode selection(MCTSNode rootMCTSNode) {
        MCTSNode node = rootMCTSNode;
        while (!node.isTerminal()) {
            // Expand if needed
            if (!node.isFullyExpanded())
                return expansion(node);
            // Else select best child and return
            else node = bestChild(node);
        }
        return node;
    }

    private MCTSNode expansion(MCTSNode node) {
        // Select next move
        Move nextMove = node.getGameState().getNextMove();
        // Make move and create child state
        MCTSNode newMCTSNode = node.createChildNodeWithMove(nextMove);
        // Add child state to the list
        node.addChild(newMCTSNode);
        return newMCTSNode;
    }

    private MCTSNode bestChild(MCTSNode node) {
        float currentValue = -Float.MAX_VALUE;
        int choiceIndex = 0;
        for (int i = 0; i < m_rootMCTSNode.getChildren().size(); i++) {
            MCTSNode child = (MCTSNode) m_rootMCTSNode.getChildren().get(i);
            // Calculate the value
            float exploitValue = (float) child.getWinCount() / (float) child.getVisitCount();
            // TODO: Check if the formula is correct
            float exploreValue =
                    m_factor * (float) Math.sqrt(Math.log( (float) node.getVisitCount() / (float) child.getVisitCount()));
            float value = exploitValue + exploreValue;
            if (value > currentValue) {
                currentValue = value;
                choiceIndex = i;
            }
            return (MCTSNode) node.getChildren().get(choiceIndex);
        }
        return null;
    }

    private Player simulation(MCTSNode leafMCTSNode) {
        GameState gameState = leafMCTSNode.getGameState().deepCopy();
        while (!gameState.isTerminal()) {
            Move nextMove = gameState.getNextMove();
            gameState.moveToNextState(nextMove);
        }
        return gameState.getWinner();
    }

    private void backPropagation(MCTSNode leafMCTSNode, Player winner) {
        MCTSNode node = leafMCTSNode;
        while (node != null) {
            node.incrementVisitCount();
            int point = 0;
            if (node.getGameState().isWinner(winner))
                // Aggregate win points
                node.incrementWinCount();

            // MCTS.Move up the tree
            node = node.getParent();
        }
    }
}