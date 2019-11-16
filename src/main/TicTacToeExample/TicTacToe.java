package TicTacToeExample;

import MCTS_Framework.MCTS;
import MCTS_Framework.MCTS_Multi;
import MCTS_Framework.Move;
import MCTS_Framework.Player;

public class TicTacToe {
    public static final Player playerX = new Player("x", 1);
    public static final Player playerO = new Player("o", 2);

    public static void main(String args[]) {
        int boardSize = 3;
        int stepSize = 2000;
        int threadCount = Runtime.getRuntime().availableProcessors() / 2;
        // Time duration in milliseconds
        long timeDuration = 250;
        float factor = 3;
        int numberOfGames = 200;
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
                    MCTS_Multi mcts_multi = new MCTS_Multi(threadCount, stepSize, timeDuration, factor, gameState);
                    Move nextMove = mcts_multi.multi_uct_search();
                    gameState.moveToNextState(nextMove);
                }
                else {
                    // Player O ( MCTS )
                    MCTS_Multi mcts_multi = new MCTS_Multi(threadCount, stepSize, timeDuration, factor, gameState);
                    Move nextMove = mcts_multi.multi_uct_search();
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
