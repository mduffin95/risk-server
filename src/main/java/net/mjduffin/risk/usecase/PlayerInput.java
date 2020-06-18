package net.mjduffin.risk.usecase;

import net.mjduffin.risk.adapters.ConsoleController;

import java.util.Map;

public interface PlayerInput {

    void registerPlayerOutput(PlayerOutput output);

    void draft(String playerName, Map<String, Integer> draft) throws GameplayException;
    void draftSingle(String playerName, String territory, int units) throws GameplayException;
    AttackResult attack(String playerName, String attackingTerritory, String defendingTerritory) throws PlayerNotFoundException, TerritoryNotFoundException, GameplayException;
    void endAttack(String playerName) throws PlayerNotFoundException;
    void fortify(String playerName, String fromTerritory, String toTerritory, int units) throws PlayerNotFoundException, TerritoryNotFoundException, GameplayException;

    GameState getGameState();
    void start(ConsoleController controller);
}
