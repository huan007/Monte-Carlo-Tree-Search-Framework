package TicTacToeExample;

import MCTS.GameState;
import MCTS.MCTS;
import MCTS.Move;
import MCTS.Player;

public class TicTacToe {
    public static final Player playerX = new Player("x", 1);
    public static final Player playerO = new Player("o", 2);

    public static void main(String args[]) {
        int boardSize = 3;
        int stepSize = 10000;
        float factor = 3;
        int numberOfGames = 1000;
        int loseCount = 0;
        for (int i = 0; i < numberOfGames; i++) {
            int[][] board = new int[boardSize][boardSize];
            TicTacToeGameState gameState = new TicTacToeGameState(playerO, board, boardSize);
            int playerNumber = 0;
            while (!gameState.isTerminal()) {
                System.out.println(gameState.toString());
                System.out.println("---------------");
                if (playerNumber == 0) {
                    // Player X ( MCTS )
                    MCTS mcts = new MCTS(stepSize, factor, gameState);
                    Move nextMove = mcts.uct_search();
                    gameState.moveToNextState(nextMove);
                }
                else {
                    // Player O ( MCTS )
                    MCTS mcts = new MCTS(stepSize, factor, gameState);
                    Move nextMove = mcts.uct_search();
                    gameState.moveToNextState(nextMove);
                }
                playerNumber = (playerNumber + 1) % 2;
            }
            System.out.println(gameState.toString());
            if (gameState.getWinner() == null)
                System.out.println("It's a draw!");
            else {
                System.out.println(String.format("Player %s win!", gameState.getWinner().getName()));
                if (gameState.getWinner().getId() == 2)
                    loseCount++;
                if (gameState.getWinner().getId() == 1)
                    loseCount++;
            }
        }
        System.out.println(String.format("MCTS Failure Rate: %f", (float) loseCount / (float) numberOfGames));
    }
}
