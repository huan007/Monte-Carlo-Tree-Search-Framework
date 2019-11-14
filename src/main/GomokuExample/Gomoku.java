package GomokuExample;

import MCTS.MCTS;
import MCTS.Move;

import java.util.Scanner;

public class Gomoku {

    public static void main(String[] args) {
        int boardSize = 19;
        int[][] board = new int[boardSize][boardSize];
        GomokuGameState gameState = new GomokuGameState(GomokuGameState.WHITE_PLAYER, board);
        int stepSize = 50000;
        // Time duration in milliseconds
        long timeDuration = 500;
        float factor = (float) 1.65;
        System.out.println(gameState.toString());
        System.out.println("---------------");
        while (!gameState.isTerminal()) {
            MCTS mcts = new MCTS(stepSize, timeDuration, factor, gameState);
            mcts.setIsVerbose(true);
            Move nextMove = mcts.uct_search();
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
