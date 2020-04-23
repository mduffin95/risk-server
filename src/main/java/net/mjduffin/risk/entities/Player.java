package net.mjduffin.risk.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {
    String name;
    private Set<Territory> territories;

    Player(String name) {
        this.name = name;
        territories = new HashSet<>();
    }

    void addTerritory(Territory t) {
        territories.add(t);
    }

    void removeTerritory(Territory t) {
        territories.remove(t);
    }

    int getTotalUnits() {
        return territories.stream().map(Territory::getUnits).reduce(0, Integer::sum);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Player)) {
            return false;
        }
        Player p = (Player) obj;
        if (this.name != null) {
            return this.name.equals(p.name);
        }
        return false;
    }
}
