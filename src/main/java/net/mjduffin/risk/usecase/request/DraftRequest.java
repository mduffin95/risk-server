package net.mjduffin.risk.usecase.request;

public class DraftRequest extends Request {
    private String player;
    private String territory;
    private int units;

    public DraftRequest(String player, String territory, int units) {
        this.player = player;
        this.territory = territory;
        this.units = units;
        this.requestType = Type.DRAFT;
    }

    public String getPlayer() {
        return player;
    }

    public String getTerritory() {
        return territory;
    }

    public int getUnits() {
        return units;
    }
}
