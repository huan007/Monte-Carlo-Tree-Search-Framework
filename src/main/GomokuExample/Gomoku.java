package GomokuExample;

import MCTS.MCTS;

import java.util.Scanner;

public class Gomoku {

    public static void main(String[] args) {
        int boardSize = 19;
        int[][] board = new int[boardSize][boardSize];
        GomokuGameState gameState = new GomokuGameState(GomokuGameState.BLACK_PLAYER, board);
        int playerNumber = 0;
        Scanner scanner = new Scanner(System.in);
        while (!gameState.isTerminal()) {
            System.out.println(gameState.toString());
            System.out.println("---------------");
            Point nextMoveCoordinates = inputToPoint(scanner.nextLine());
            if (nextMoveCoordinates == null)
                continue;
            GomokuMove nextMove = new GomokuMove(nextMoveCoordinates.toString(), nextMoveCoordinates);
            gameState.moveToNextState(nextMove);
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
