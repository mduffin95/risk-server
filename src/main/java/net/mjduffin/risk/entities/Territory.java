package net.mjduffin.risk.entities;

public class Territory {
    private String name;
    private Player player;
    private int units;

    Territory(String name) {
        this.name = name;
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

    public void addUnits(int num) {
        units += Math.abs(num);
    }

    public void subtractUnits(int num) {
        units -= Math.abs(num);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player, int units) {
        this.player = player;
    }

    //TODO: Implement equals and hashCode
}
