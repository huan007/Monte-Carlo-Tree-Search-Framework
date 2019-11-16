package GomokuExample;

import MCTS_Framework.MCTS;
import MCTS_Framework.MCTS_Multi;
import MCTS_Framework.Move;

public class Gomoku {

    public static void main(String[] args) {
        int boardSize = 19;
        int[][] board = new int[boardSize][boardSize];
        GomokuGameState gameState = new GomokuGameState(GomokuGameState.WHITE_PLAYER, board);
        int stepSize = 3000;
        int threadCount = Runtime.getRuntime().availableProcessors() / 2;
        // Time duration in milliseconds
        long timeDuration = 500;
        float factor = (float) 0.8;
        System.out.println(gameState.toString());
        System.out.println("---------------");
        while (!gameState.isTerminal()) {
            MCTS_Multi mcts_multi = new MCTS_Multi(threadCount, stepSize, timeDuration, factor, gameState);
            mcts_multi.setIsVerbose(true);
            Move nextMove = mcts_multi.multi_uct_search();
            gameState.moveToNextState(nextMove);
            System.out.println(String.format("Next move: %s", nextMove.toString()));
            System.out.println(gameState.toString());
            System.out.println("---------------");
        }
    }

    public static Point inputToPoint(String input) {
        String[] values = input.split(",");
        if (values.length > 2)
            return null;
        try {
            return new Point(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
