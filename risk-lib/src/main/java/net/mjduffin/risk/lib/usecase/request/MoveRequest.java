package net.mjduffin.risk.lib.usecase.request;

public class MoveRequest extends Request {
    private String playerName;
    private int units;

    public MoveRequest(String player, int num) {
        this.playerName = player;
        this.units = num;
        this.requestType = Type.MOVE;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getUnits() {
        return units;
    }
}
