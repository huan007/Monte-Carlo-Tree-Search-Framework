package MCTS;

import java.util.List;
import java.util.Random;

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
        // Pop a random next possible move and return it
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