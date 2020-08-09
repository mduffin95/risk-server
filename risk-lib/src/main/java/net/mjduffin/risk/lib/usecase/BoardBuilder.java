package net.mjduffin.risk.lib.usecase;

import net.mjduffin.risk.lib.entities.Board;
import net.mjduffin.risk.lib.entities.Territory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardBuilder {
    private Map<String, Territory> territories = new HashMap<>();
    private Map<Territory, List<Territory>> adjTerritories = new HashMap<>();

    public BoardBuilder addTerritory(String name) {
        Territory territory = territories.get(name);
        if (territory == null) {
            territory = new Territory(name);
            territories.put(name, territory);
        }
        adjTerritories.putIfAbsent(territory, new ArrayList<>());
        return this;
    }

    public BoardBuilder addEdge(String territory1, String territory2) {
        Territory t1 = territories.get(territory1);
        Territory t2 = territories.get(territory2);
        adjTerritories.get(t1).add(t2);
        adjTerritories.get(t2).add(t1);
        return this;
    }

    public Board build() {
        return new Board(territories, adjTerritories);
    }
}
