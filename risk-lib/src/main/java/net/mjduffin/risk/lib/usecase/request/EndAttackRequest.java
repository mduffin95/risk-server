package net.mjduffin.risk.lib.usecase.request;

public class EndAttackRequest extends Request {
    private String playerName;

    public EndAttackRequest(String player) {
        this.playerName = player;
        requestType = Type.ENDATTACK;
    }

    public String getPlayerName() {
        return playerName;
    }
}
