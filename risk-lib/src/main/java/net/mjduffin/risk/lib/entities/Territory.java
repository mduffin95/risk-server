package net.mjduffin.risk.lib.entities;

public class Territory {
    private String name;
    private Player player;
    private int units;

    public Territory(String name, Player player) {
        this.name = name;
        this.player = player;
        this.units = 1;
    }

    public void init(Player player) {
        this.player = player;
        player.addTerritory(this);
    }

    public String getName() {
        return name;
    }

    public int getUnits() {
        return units;
    }

    public int getAvailableUnits() {
        return getUnits() - 1;
    }

    public void addUnits(int num) {
        units += Math.abs(num);
    }

    public void subtractUnits(int num) {
        if (num <= units) {
            units -= Math.abs(num);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player, int units) {
        this.player = player;
    }

    //TODO: Implement equals and hashCode
}
