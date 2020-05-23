package net.mjduffin.risk.usecase;

import java.util.Map;

public interface PlayerInput {

    void draft(String playerName, Map<String, Integer> draft);
    AttackResult attack(String playerName, String attackingTerritory, String defendingTerritory) throws PlayerNotFoundException, TerritoryNotFoundException, GameplayException;
    void endAttack(String playerName) throws PlayerNotFoundException;
    void fortify(String playerName, String fromTerritory, String toTerritory, int units) throws PlayerNotFoundException, TerritoryNotFoundException;
}
