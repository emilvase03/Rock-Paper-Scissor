package rps.bll.player;

// Project imports
import rps.bll.game.IGameState;
import rps.bll.game.Move;
import rps.bll.game.Result;

// Java imports
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot implements IPlayer {
    private String name;
    private PlayerType type;
    private final Random random = new Random();

    // idx 0 = rock, 1 = paper, 2 = scissor
    private final int[][] transitionMatrix = new int[3][3];

    private int lastProcessedIndex = 0;

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
        List<Result> history = new ArrayList<>(state.getHistoricResults());

        updateMatrix(history);

        if (history.isEmpty()) {
            return getRandomMove();
        }

        Move lastOpponentMove = getOpponentMoveFromResult(history.get(history.size() - 1));
        int lastIdx = lastOpponentMove.ordinal();

        int predictedIdx = 0;
        int maxCount = -1;

        for (int i = 0; i < 3; i++) {
            if (transitionMatrix[lastIdx][i] > maxCount) {
                maxCount = transitionMatrix[lastIdx][i];
                predictedIdx = i;
            }
        }

        if (maxCount <= 0) {
            return getRandomMove();
        }

        Move predictedOpponentMove = Move.values()[predictedIdx];
        return getCounterMove(predictedOpponentMove);
    }

    private void updateMatrix(List<Result> history) {
        while (lastProcessedIndex < history.size() - 1) {
            Result firstRound = history.get(lastProcessedIndex);
            Result secondRound = history.get(lastProcessedIndex + 1);

            int prevMoveIdx = getOpponentMoveFromResult(firstRound).ordinal();
            int nextMoveIdx = getOpponentMoveFromResult(secondRound).ordinal();

            transitionMatrix[prevMoveIdx][nextMoveIdx]++;
            lastProcessedIndex++;
        }
    }

    private Move getOpponentMoveFromResult(Result result) {
        if (result.getWinnerPlayer().getPlayerType() == PlayerType.Human) {
            return result.getWinnerMove();
        } else {
            return result.getLoserMove();
        }
    }

    private Move getCounterMove(Move move) {
        switch (move) {
            case Rock: return Move.Paper;
            case Paper: return Move.Scissor;
            case Scissor: return Move.Rock;
            default: return getRandomMove();
        }
    }

    private Move getRandomMove() {
        return Move.values()[random.nextInt(3)];
    }
}