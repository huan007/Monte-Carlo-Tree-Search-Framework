package MCTS_Framework;

import java.util.List;

public class MCTS {
    private int m_iterationsCount;
    private int m_stepCount;
    private long m_timeDuration;
    private int m_globalVisitCount;
    private float m_factor;
    private MCTSNode m_rootMCTSNode;
    private List<Player> m_possiblePlayers;
    private List<Move> m_possibleMoves;
    private RewardFunctionInterface m_rewardFunction;
    private boolean m_isVerbose;

    public static class Builder {

    }

    protected MCTS() {
        m_globalVisitCount = 0;
        m_stepCount = 100;
        m_rootMCTSNode = new MCTSNode(null);
    }

    public MCTS(int iterationsCount, long timeDuration, float factor, GameState gameState) {
        m_globalVisitCount = 0;
        m_stepCount = 100;
        m_iterationsCount = iterationsCount;
        m_timeDuration = timeDuration;
        m_factor = factor;
        m_rootMCTSNode = new MCTSNode(gameState, null);
        m_rewardFunction = new RewardFunctionInterface() {
            @Override
            public float calculateReward(GameState gameState) {
                return 1;
            }
        };
        m_isVerbose = false;
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
        long currentTime = System.currentTimeMillis();
        long expireTime = currentTime + m_timeDuration;
        for (int iteration = 0; iteration < m_iterationsCount; iteration++) {
            MCTSNode leafMCTSNode = selection(m_rootMCTSNode, iteration);
            SimulationResult result = simulation(leafMCTSNode);
            backPropagation(leafMCTSNode, result.getWinner(), result.getRewardValue());
        }
        while (System.currentTimeMillis() < expireTime) {
            for (int iteration = 0; iteration < m_stepCount; iteration++) {
                MCTSNode leafMCTSNode = selection(m_rootMCTSNode, iteration);
                SimulationResult result = simulation(leafMCTSNode);
                backPropagation(leafMCTSNode, result.getWinner(), result.getRewardValue());
            }
        }
        return getBestMove();
    }

    public Move getBestMove() {
        float maxValue = -Float.MAX_VALUE;
        int choiceIndex = 0;
        for (int i = 0; i < m_rootMCTSNode.getChildren().size(); i++) {
            MCTSNode child = (MCTSNode) m_rootMCTSNode.getChildren().get(i);
            float value = (float) child.getWinCount() / (float) child.getVisitCount();
            // Print debugging outputs
            if (m_isVerbose) {
                System.out.println(String.format("%s: %f / %d", child.getLastMove().getMoveName(),
                        child.getWinCount(), child.getVisitCount()));
            }
            if (value > maxValue) {
                maxValue = value;
                choiceIndex = i;
            }
        }
        return (m_rootMCTSNode.getChildren().get(choiceIndex)).getLastMove();
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
                    m_factor * (float) Math.sqrt(Math.log( (float) node.getVisitCount() / (float) child.getVisitCount()));
            float value = exploitValue + exploreValue;
            if (exploreValue > exploitValue) {
                int a = 0;
            }
            if (value > currentValue) {
                currentValue = value;
                choiceIndex = i;
            }
        }
        return (MCTSNode) node.getChildren().get(choiceIndex);
    }

    private SimulationResult simulation(MCTSNode leafMCTSNode) {
        GameState gameState = leafMCTSNode.getGameState().deepCopy();
        while (!gameState.isTerminal()) {
            Move nextMove = gameState.getNextMove();
            gameState.moveToNextState(nextMove);
        }
        SimulationResult result =
                new SimulationResult(gameState.getWinner(), m_rewardFunction.calculateReward(gameState));
        return result;
    }

    private void backPropagation(MCTSNode leafMCTSNode, Player winner, float rewardPoint) {
        MCTSNode node = leafMCTSNode;
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

    public MCTSNode getRootMCTSNode() {
        return m_rootMCTSNode;
    }

    public void setRewardFunction(RewardFunctionInterface rewardFunction) {
        m_rewardFunction = rewardFunction;
    }

    public void setIsVerbose(boolean isVerbose) {
        m_isVerbose = isVerbose;
    }

    public interface RewardFunctionInterface<T extends GameState> {
        float calculateReward(T gameState);
    }
}
