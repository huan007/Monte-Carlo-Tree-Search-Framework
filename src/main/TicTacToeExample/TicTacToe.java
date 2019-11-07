package TicTacToeExample;

import MCTS.GameState;
import MCTS.Move;

public class TicTacToe {

    public class TicTacToeGameState extends GameState {

        @Override
        public GameState deepCopy() {
            return null;
        }

        @Override
        protected void makeMove(Move move) {

        }

        @Override
        protected void determineTerminalAndWinner() {

        }

        @Override
        protected void generateNextPossibleMoves() {

        }
    }
}
