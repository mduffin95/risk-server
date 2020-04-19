package net.mjduffin.risk.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {
    private Set<Territory> territories;

    Player() {
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

}
