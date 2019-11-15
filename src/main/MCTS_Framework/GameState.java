package MCTS_Framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GameState {
    protected boolean m_isTerminal;
    protected Player m_player;
    protected Player m_nextPlayer;
    protected Player m_winner;
    protected List<Move> m_possibleMoves;

    public abstract GameState deepCopy();
    protected abstract void makeMove(Move move);
    protected abstract Player determineNextPlayer();
    protected abstract boolean determineTerminal();
    protected abstract Player determineWinner();
    protected abstract void generateNextPossibleMoves();
    protected abstract boolean isSuccessful(Player winner);

    public GameState(Player player) {
        setPlayer(player);
        m_isTerminal = false;
        m_winner = null;
        m_possibleMoves = new ArrayList<Move>();
    }

    public GameState(GameState oldState) {
        this.m_isTerminal = oldState.m_isTerminal;
        this.m_player = oldState.m_player;
        this.m_nextPlayer = oldState.m_nextPlayer;
        this.m_winner = oldState.m_winner;
        this.m_possibleMoves = new ArrayList<Move>(oldState.m_possibleMoves);
    }

    public Player getPlayer() {
        return m_player;
    }

    public Player getNextPlayer() {
        return m_nextPlayer;
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
        // Set Current Player and update nextPlayer
        m_player = player;
        m_nextPlayer = determineNextPlayer();
    }

    public void setWinner(Player winner) {
        m_winner = winner;
    }

    public void moveToNextState(Move move) {
        makeMove(move);
        setPlayer(m_nextPlayer);
        m_isTerminal = determineTerminal();
        // Only determine winner if terminal
        if (isTerminal())
            m_winner = determineWinner();
        generateNextPossibleMoves();
    }

    public boolean isTerminal() {
        return m_isTerminal;
    }

    public boolean isWinner(Player winner) {
        return m_player.equals(winner);
    }
}