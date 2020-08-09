package net.mjduffin.risk.usecase.request;

public class FortifyRequest extends Request {
    private String playerName;
    private String fromTerritory;
    private String toTerritory;
    private int units;

    public FortifyRequest(String player, String fromTerritory, String toTerritory, int num) {
        this.playerName = player;
        this.fromTerritory = fromTerritory;
        this.toTerritory = toTerritory;
        this.units = num;
        this.requestType = Type.FORTIFY;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getFromTerritory() {
        return fromTerritory;
    }

    public String getToTerritory() {
        return toTerritory;
    }

    public int getUnits() {
        return units;
    }
}
