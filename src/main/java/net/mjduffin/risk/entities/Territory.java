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

    void addUnits(int num) {
        units += Math.abs(num);
    }

    void subtractUnits(int num) {
        units -= Math.abs(num);
    }

    //TODO: Implement equals and hashCode
}
