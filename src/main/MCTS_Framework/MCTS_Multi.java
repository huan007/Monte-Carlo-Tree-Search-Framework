package MCTS_Framework;

import java.util.HashMap;
import java.util.Map;

public class MCTS_Multi {
    private MCTS.RewardFunctionInterface m_rewardFunction;
    private boolean m_isCustomRewardFunction;
    private boolean m_isVerbose;
    MCTS[] m_mctsList;
    Thread[] m_threads;

    // Parameters
    int m_iterationsCount;
    long m_timeDuration;
    float m_factor;
    GameState m_rootGameState;

    public MCTS_Multi(int threadCount, int iterationsCount, long timeDuration, float factor, GameState gameState) {
        m_isCustomRewardFunction = false;
        m_isVerbose = false;
        m_mctsList = new MCTS[threadCount];
        m_threads = new Thread[threadCount];
        m_iterationsCount = iterationsCount;
        m_timeDuration = timeDuration;
        m_factor = factor;
        m_rootGameState = gameState.deepCopy();
    }

    public Move multi_uct_search() {
        // Create and dispatch MCTS instances
        for (int i = 0; i < m_threads.length; i++) {
            MCTS newMCTS = new MCTS(m_iterationsCount, m_timeDuration, m_factor, m_rootGameState);
            if (m_isCustomRewardFunction)
                newMCTS.setRewardFunction(m_rewardFunction);
            Thread t = new Thread(new MCTS_Instance(newMCTS));
            m_threads[i] = t;
            m_mctsList[i] = newMCTS;
            t.start();
        }

        // Wait (Joining) for all instances to finish
        for (int i = 0; i < m_threads.length; i++) {
            try {
                m_threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MCTS dummyMCTS = joinMCTS(m_mctsList);
        return dummyMCTS.getBestMove();
    }

    public MCTS joinMCTS(MCTS[] mctsList) {
        // Creating dummy MCTS to aggregate every other MCTS into
        MCTS newMCTS = new MCTS();
        newMCTS.setIsVerbose(m_isVerbose);
        // Keep track of unique children. Each MCTSNode will be identified by gameState, which use toString() to uniquely
        // identify the game state
        HashMap<String, MCTSNode> childrenMap = new HashMap<>();
        // For each instance of MCTS
        for (MCTS mcts : mctsList) {
            // For each child in the instance
            for (MCTSNode child : mcts.getRootMCTSNode().getChildren()) {
                // If child is not on the list, create a copy of the child with the same stats
                if (!childrenMap.containsKey(child.toString())) {
                    MCTSNode newChild = new MCTSNode(newMCTS.getRootMCTSNode());
                    newChild.setVisitCount(child.getVisitCount());
                    newChild.setWinCount(child.getWinCount());
                    newChild.setLastMove(child.getLastMove());
                    childrenMap.put(child.toString(), newChild);
                }
                // If child is already on the list, aggregate the stats
                else {
                    MCTSNode childOnList = childrenMap.get(child.toString());
                    childOnList.setVisitCount(childOnList.getVisitCount() + child.getVisitCount());
                    childOnList.setWinCount(childOnList.getWinCount() + child.getWinCount());
                }
            }
        }

        // Add unique children to dummy MCTS
        for (Map.Entry<String, MCTSNode> e : childrenMap.entrySet()) {
            MCTSNode child = e.getValue();
            newMCTS.getRootMCTSNode().addChild(child);
        }

        return newMCTS;
    }

    public void setRewardFunction(MCTS.RewardFunctionInterface rewardFunction) {
        if (rewardFunction == null)
            m_isCustomRewardFunction = false;
        else {
            m_isCustomRewardFunction = true;
            m_rewardFunction = rewardFunction;
        }
    }

    public void setIsVerbose(boolean isVerbose) {
        m_isVerbose = isVerbose;
    }
}
