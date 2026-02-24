package rps.bll.player;

import rps.bll.game.IGameState;
import rps.bll.game.Move;

public class Bot implements IPlayer {
    private String name;
    private PlayerType type;

    /**
     * @param name
     */
    public Bot(String name, PlayerType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getPlayerName() {
        return "";
    }

    @Override
    public PlayerType getPlayerType() {
        return null;
    }

    @Override
    public Move doMove(IGameState state) {
        return null;
    }
}
