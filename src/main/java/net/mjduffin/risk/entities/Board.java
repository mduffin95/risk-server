package net.mjduffin.risk.entities;

import java.util.*;

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

    public List<Territory> getTerritories() {
        return new ArrayList<>(territories.values());
    }


    public void initialiseWithPlayers(List<Player> players) {
        List<Territory> allTerritories = new ArrayList<>(territories.values());

        Collections.shuffle(allTerritories);

        int i = 0;
        for (Territory t: allTerritories) {
            Player p = players.get(i);
            t.init(p);
            i = (i+1) % players.size();
        }

    }
}
