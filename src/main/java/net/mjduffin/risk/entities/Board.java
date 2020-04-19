package net.mjduffin.risk.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    private Map<String, Territory> territories;
    private Map<Territory, List<Territory>> adjTerritories;

    Board() {
        territories = new HashMap<>();
        adjTerritories = new HashMap<>();
    }

    void removeTerritory(String label) {
        Territory v = territories.get(label);
        if (v != null) {
            adjTerritories.values().stream().forEach(e -> e.remove(v));
            adjTerritories.remove(new Territory(label));
        }
    }

    Territory getOrCreateTerritory(String name) {
        Territory territory = territories.get(name);
        if (territory == null) {
            territory = new Territory(name);
            territories.put(name, territory);
        }
        adjTerritories.putIfAbsent(territory, new ArrayList<>());
        return territory;
    }

    void addEdge(Territory t1, Territory t2) {
        adjTerritories.get(t1).add(t2);
        adjTerritories.get(t2).add(t1);
    }

    boolean areAdjacent(Territory a, Territory b) {
        if (adjTerritories.get(a).contains(b)) {
            assert adjTerritories.get(b).contains(a);
            return true;
        }
        return false;
    }
}
