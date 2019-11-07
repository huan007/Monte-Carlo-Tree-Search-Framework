import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        MCTSNode MCTSNode = rootMCTSNode;
        while (!MCTSNode.isTerminal()) {
            // Expand if needed
            if (!MCTSNode.isFullyExpanded())
                return expansion(MCTSNode);
            // Else select best child and return
            else MCTSNode = bestChild(MCTSNode);
        }
        return MCTSNode;
    }

    private MCTSNode expansion(MCTSNode MCTSNode) {
        // Select next move
        Move nextMove = MCTSNode.m_gameState.getNextMove();
        // Make move and create child state
        MCTSNode newMCTSNode = MCTSNode.createChildNodeWithMove(nextMove);
        // Add child state to the list
        MCTSNode.addChild(newMCTSNode);
        return newMCTSNode;
    }

    private MCTSNode bestChild(MCTSNode MCTSNode) {
        float currentValue = -Float.MAX_VALUE;
        int choiceIndex = 0;
        for (int i = 0; i < m_rootMCTSNode.getChildren().size(); i++) {
            MCTSNode child = (MCTSNode) m_rootMCTSNode.getChildren().get(i);
            // Calculate the value
            float exploitValue = (float) child.getWinCount() / (float) child.getVisitCount();
            // TODO: Check if the formula is correct
            float exploreValue =
                    m_factor * (float) Math.sqrt(Math.log( (float) MCTSNode.getVisitCount() / (float) child.getVisitCount()));
            float value = exploitValue + exploreValue;
            if (value > currentValue) {
                currentValue = value;
                choiceIndex = i;
            }
            return (MCTSNode) MCTSNode.getChildren().get(choiceIndex);
        }
        return null;
    }

    private Player simulation(MCTSNode leafMCTSNode) {
        GameState gameState = leafMCTSNode.m_gameState.deepCopy();
        while (!gameState.isTerminal()) {
            Move nextMove = gameState.getNextMove();
            gameState.moveToNextState(nextMove);
        }
        return gameState.getWinner();
    }

    private void backPropagation(MCTSNode leafMCTSNode, Player winner) {
        MCTSNode MCTSNode = leafMCTSNode;
        while (MCTSNode != null) {
            MCTSNode.incrementVisitCount();
            int point = 0;
            if (MCTSNode.m_gameState.isWinner(winner))
                // Aggregate win points
                MCTSNode.incrementWinCount();

            // Move up the tree
            MCTSNode = MCTSNode.getParent();
        }
    }

    public class MCTSNode {
        private int m_visitCount;
        private int m_winCount;
        private MCTSNode m_parent;
        private Move m_lastMove;
        private List<MCTSNode> m_children;
        private GameState m_gameState;

        public MCTSNode(GameState gameState) {
            m_visitCount = 0;
            m_winCount = 0;
            m_parent = null;
            m_lastMove = null;
            m_children = new ArrayList<>();
            m_gameState = gameState;
        }

        public List<MCTSNode> getChildren() {
            return m_children;
        }

        public Move getLastMove() {
            return m_lastMove;
        }

        public int getWinCount() {
            return m_winCount;
        }

        public int getVisitCount() {
            return m_visitCount;
        }

        public MCTSNode getParent() {
            return m_parent;
        }

        public void incrementVisitCount() {
            m_visitCount++;
        }

        public void incrementWinCount() {
            m_winCount++;
        }

        public void addChild(MCTSNode childMCTSNode) {
            m_children.add(childMCTSNode);
        }

        public MCTSNode createChildNodeWithMove(Move move) {
            GameState nextGameState = m_gameState.deepCopy();
            nextGameState.moveToNextState(move);
            MCTSNode newMCTSNode = new MCTSNode(nextGameState);
            newMCTSNode.setLastMove(move);
            return newMCTSNode;
        }

        public void setLastMove(Move move) {
            m_lastMove = move;
        }

        public boolean isTerminal() {
            return m_gameState.isTerminal();
        }

        public boolean isFullyExpanded() {
            // If there are not any possible moves left, then node is fully expanded
            return m_possibleMoves.size() == 0;
        }
    }

    public abstract class GameState {
        private boolean m_isTerminal;
        private Player m_player;
        private Player m_winner;
        private List<Move> m_possibleMoves;

        public abstract GameState deepCopy();
        protected abstract void makeMove(Move move);
        protected abstract void determineTerminalAndWinner();
        protected abstract void generateNextPossibleMoves();


        public Player getPlayer() {
            return m_player;
        }

        public Player getWinner() {
            return m_winner;
        }

        public Move getNextMove() {
            List<Move> possibleMoves = getPossibleMoves();
            int choiceIndex = new Random().nextInt(possibleMoves.size());
            Move nextMove = possibleMoves.remove(choiceIndex);
            return nextMove;
        }

        public List<Move> getPossibleMoves() {
            return m_possibleMoves;
        }

        public void setPlayer(Player player) {
            m_player = player;
        }

        public void moveToNextState(Move move) {
            makeMove(move);
            determineTerminalAndWinner();
            generateNextPossibleMoves();
        }

        public boolean isTerminal() {
            return m_isTerminal;
        }

        public boolean isWinner(Player winner) {
            return m_player.equals(winner);
        }
    }

    public class Player {
        private String m_name;
        private int m_id;
        public Player(String name, int id) {
            m_name = name;
            m_id = id;
        }

        public String getName() {
            return m_name;
        }

        public int getId() {
            return m_id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Player))
                return false;
            Player otherPlayer = (Player) obj;
            return m_id == otherPlayer.m_id;
        }
    }

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
}
