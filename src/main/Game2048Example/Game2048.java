package Game2048Example;

import MCTS.MCTS;
import MCTS.Move;

public class Game2048 {

    public static void main(String[] args) {
        // MCTS Parameters
        int stepSize = 20000;
        float factor = (float) 100;
        int numberOfGames = 100;
        // Time duration in milliseconds
        long timeDuration = 2000;

        // Game Parameters
        int boardSize = 4;
        int[][] boardValues = new int[4][4];
        GameState2048.placeRandomTile(boardValues, boardSize);
        GameState2048.placeRandomTile(boardValues, boardSize);
        GameState2048 gameState = new GameState2048(GameState2048.player, boardValues, boardSize, 0, 0);
        System.out.println(gameState.toString());
        while (!gameState.isTerminal()) {
            MCTS mcts = new MCTS(stepSize, timeDuration, factor, gameState);
            mcts.setRewardFunction(GameState2048.rewardFunction);
            mcts.setIsVerbose(true);
            Move nextMove = mcts.uct_search();
            gameState.moveToNextState(nextMove);
            System.out.println(String.format("Best Move: %s", nextMove.getMoveName()));
            System.out.println("---------------");
            System.out.println(gameState.toString());
        }
    }
}
