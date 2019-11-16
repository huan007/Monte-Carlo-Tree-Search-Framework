package Game2048Example;

import MCTS_Framework.MCTS;
import MCTS_Framework.MCTS_Multi;
import MCTS_Framework.Move;

public class Game2048 {

    public static void main(String[] args) {
        // MCTS Parameters
        int threadCount = 6;
        int stepSize = 4000;
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
            MCTS_Multi mcts_multi = new MCTS_Multi(threadCount, stepSize, timeDuration, factor, gameState);
            mcts_multi.setRewardFunction(GameState2048.rewardFunction);
            mcts_multi.setIsVerbose(true);
            Move nextMove = mcts_multi.multi_uct_search();
            gameState.moveToNextState(nextMove);
            System.out.println(String.format("Best Move: %s", nextMove.getMoveName()));
            System.out.println("---------------");
            System.out.println(gameState.toString());
        }
    }
}
