package MCTS;

import java.util.List;

public class MCTS {
    private int m_stepSize;
    private int m_globalVisitCount;
    private float m_factor;
    private MCTSNode m_rootMCTSNode;
    private List<Player> m_possiblePlayers;
    private List<Move> m_possibleMoves;
    private RewardFunctionInterface m_rewardFunction;

    public static class Builder {

    }

    public MCTS(int stepSize, float factor, GameState gameState) {
        m_globalVisitCount = 0;
        m_stepSize = stepSize;
        m_factor = factor;
        m_rootMCTSNode = new MCTSNode(gameState, null);
        m_rewardFunction = new RewardFunctionInterface() {
            @Override
            public float calculateReward(GameState gameState) {
                return 1;
            }
        };
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
            MCTSNode leafMCTSNode = selection(m_rootMCTSNode, iteration);
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

    private MCTSNode selection(MCTSNode rootMCTSNode, int globalVisitCount) {
        MCTSNode node = rootMCTSNode;
        while (!node.isTerminal()) {
            // Expand if needed
            if (!node.isFullyExpanded())
                return expansion(node);
            // Else select best child and return
            else node = bestChild(node, globalVisitCount);
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

    private MCTSNode bestChild(MCTSNode node, int globalVisitCount) {
        float currentValue = -Float.MAX_VALUE;
        int choiceIndex = 0;
        for (int i = 0; i < node.getChildren().size(); i++) {
            MCTSNode child = (MCTSNode) node.getChildren().get(i);
            // Calculate the value
            float exploitValue = (float) child.getWinCount() / (float) child.getVisitCount();
            // TODO: Check if the formula is correct
            float exploreValue =
                    m_factor * (float) Math.sqrt(Math.log( (float) globalVisitCount / (float) child.getVisitCount()));
            float value = exploitValue + exploreValue;
            if (value > currentValue) {
                currentValue = value;
                choiceIndex = i;
            }
        }
        return (MCTSNode) node.getChildren().get(choiceIndex);
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
        float rewardPoint = m_rewardFunction.calculateReward(leafMCTSNode.getGameState());
        while (node != null) {
            node.incrementVisitCount();
            int point = 0;
            if (node.getGameState().isSuccessful(winner))
                // Aggregate win points
                node.incrementWinCount(rewardPoint);

            // MCTS.Move up the tree
            node = node.getParent();
        }
    }

    public void setRewardFunction(RewardFunctionInterface rewardFunction) {
        m_rewardFunction = rewardFunction;
    }

    public interface RewardFunctionInterface {
        float calculateReward(GameState gameState);
    }
}
