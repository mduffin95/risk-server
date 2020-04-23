package net.mjduffin.risk.usecase;

import java.util.Map;

public interface PlayerInput {

    void draft(String playerName, Map<String, Integer> draft);
    void attack(String playerName, String attackingTerritory, String defendingTerritory);
    void endAttack(String playerName);
    void fortify(String playerName, String fromTerritory, String toTerritory, int units);
}
