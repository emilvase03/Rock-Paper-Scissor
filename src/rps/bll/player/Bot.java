package rps.bll.player;

import rps.bll.game.IGameState;
import rps.bll.game.Move;
import rps.bll.game.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot implements IPlayer {
    private String name;
    private PlayerType type;

    private final int[][] transitionMatrix = new int[3][3];

    public Bot(String name, PlayerType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    @Override
    public PlayerType getPlayerType() {
        return type;
    }

    @Override
    public Move doMove(IGameState state) {
        ArrayList<Result> history = (ArrayList<Result>) state.getHistoricResults();

        if (history.size() < 2) {
            return getRandomMove();
        }

        updateMatrix(history);

        Move lastOpponentMove = getOpponentMoveFromResult(history.get(history.size() - 1));
        int lastIdx = lastOpponentMove.ordinal();

        int predictedIdx = 0;
        for (int i = 1; i < 3; i++) {
            if (transitionMatrix[lastIdx][i] > transitionMatrix[lastIdx][predictedIdx]) {
                predictedIdx = i;
            }
        }

        if (transitionMatrix[lastIdx][predictedIdx] == 0) {
            return getRandomMove();
        }

        Move predictedOpponentMove = Move.values()[predictedIdx];
        return getCounterMove(predictedOpponentMove);
    }

    private void updateMatrix(List<Result> history) {
        Result latestResult = history.get(history.size() - 1);
        Result previousResult = history.get(history.size() - 2);

        int prevIdx = getOpponentMoveFromResult(previousResult).ordinal();
        int currentIdx = getOpponentMoveFromResult(latestResult).ordinal();

        transitionMatrix[prevIdx][currentIdx]++;
    }

    private Move getOpponentMoveFromResult(Result result) {
        if (result.getWinnerPlayer().getPlayerType() == PlayerType.Human) {
            return result.getWinnerMove();
        } else {
            return result.getLoserMove();
        }
    }

    private Move getCounterMove(Move move) {
        if (move == Move.Rock) return Move.Paper;
        if (move == Move.Paper) return Move.Scissor;
        return Move.Rock;
    }

    private Move getRandomMove() {
        return Move.values()[new Random().nextInt(3)];
    }
}