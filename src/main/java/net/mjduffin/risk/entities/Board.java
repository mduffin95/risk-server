package net.mjduffin.risk.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    private Map<String, Territory> territories;
    private Map<Territory, List<Territory>> adjTerritories;

    public Board(Map<String, Territory> territories, Map<Territory, List<Territory>> adjTerritories) {
        this.territories = territories;
        this.adjTerritories = adjTerritories;
    }

    boolean areAdjacent(Territory a, Territory b) {
        if (adjTerritories.get(a).contains(b)) {
            assert adjTerritories.get(b).contains(a);
            return true;
        }
        return false;
    }

    public boolean areConnected(Territory a, Territory b) {
        //TODO: Use BFS to check if two territories are connected
        return true;
    }

    public Territory getTerritory(String name) {
        return territories.get(name);
    }
}
