package net.mjduffin.risk.usecase;

import net.mjduffin.risk.adapters.ConsoleController;
import net.mjduffin.risk.entities.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.mjduffin.risk.entities.Game.State.*;

class GameManager implements PlayerInput {
    private Game game;
    private DiceManager diceManager;
    private int leftToDraft;
    private final int TOTAL_UNITS = 60;
    Map<Player, Integer> draftRemaining = new HashMap<>();

    Map<String, PlayerOutput> outputMap = new HashMap<>();

    GameManager(Game game, DiceManager diceManager) {
        this.game = game; //Game is fully populated
        this.diceManager = diceManager;
        leftToDraft = game.getNumPlayers();
    }

    @Override
    public void registerPlayerOutput(PlayerOutput output) {
        outputMap.put(output.getPlayerName(), output);
    }

    private boolean isPlayerTurn(Player player) {
        if (player.equals(game.getCurrentPlayer()) || game.getState().equals(ALLDRAFT)) {
            return true;
        } else {
            return false;
        }
    }

    private int getDraftAllowance(Player player) {
        //If ALLDRAFT, return 10, otherwise calculate from territories owned
        if (game.getState().equals(ALLDRAFT)) {
            return TOTAL_UNITS / game.getNumPlayers();
        } else {
            //Calculate based on territories owned
            return 10;
        }
    }

    private int getRemainingDraft(Player player) {
        return draftRemaining.getOrDefault(player, getDraftAllowance(player));
    }

    private boolean finishedDrafting() {
        return draftRemaining.values().stream().allMatch(x -> x == 0);
    }

    private boolean finishedDrafting(Player player) {
        return draftRemaining.get(player) == null || draftRemaining.get(player) == 0;
    }

    private void draftUnits(Territory territory, Player player, int units) throws GameplayException {

        int remaining = getRemainingDraft(player);

        if (remaining > 0 && units <= remaining) {
            addUnitsToTerritory(territory, player, units);

            //Add new units on
            draftRemaining.put(player, remaining - units);
        } else {
            throw new GameplayException("Not enough units");
        }
    }

    private void addUnitsToTerritory(Territory territory, Player player, int units) throws GameplayException {
        if (territory.getPlayer().equals(player)) {
            territory.addUnits(units);
        } else {
            throw new GameplayException("Wrong player");
        }
    }

    //Must be called with entire draft at once
    public void draft(String playerName, Map<String, Integer> draft) throws GameplayException {
        //Verify that it's player's go (or ALL_DRAFT)
        Player player = game.getPlayer(playerName);
        if (!isPlayerTurn(player)) {
            throw new GameplayException("Not player's turn");
        }

        //Verify total number of troops is correct

        for (Map.Entry<String, Integer> e: draft.entrySet()) {
            String territoryName = e.getKey();
            Integer units = e.getValue();

            if (units != null) {
                Territory t = game.getBoard().getTerritory(territoryName);
                draftUnits(t, player, units);
            }

        }

        if (game.getState() == ALLDRAFT) {
            game.nextPlayer();
            if (finishedDrafting()) {
                game.nextState();
            }
        } else if (game.getState() == DRAFT && finishedDrafting(player)) {
            game.nextState();
        }
    }

    @Override
    public void draftSingle(String playerName, String territoryName, int units) throws GameplayException {
        //Verify that it's player's go (or ALL_DRAFT)
        Player player = game.getPlayer(playerName);
        if (!isPlayerTurn(player)) {
            throw new GameplayException("Not player's turn");
        }

        Territory territory = game.getBoard().getTerritory(territoryName);
        draftUnits(territory, player, units);

        if (game.getState() == ALLDRAFT) {
            if (finishedDrafting(player)) {
                if (finishedDrafting()) {
                    game.nextState();
                }
                game.nextPlayer();
            }
        } else if (game.getState() == DRAFT && finishedDrafting(player)) {
            game.nextState();
        }
    }

    public AttackResult attack(String playerName, String attackingTerritory, String defendingTerritory) throws PlayerNotFoundException, TerritoryNotFoundException, GameplayException {
        //Verify that game is in attack phase it's player's go
        if (game.getState() != ATTACK) {
            throw new GameplayException("Not in attack phase");
        }
        Player player = getPlayer(playerName);
        if (!player.equals(game.getCurrentPlayer())) {
            throw new GameplayException("Not current player");
        }

        Territory attacker = getTerritory(attackingTerritory);
        Territory defender = getTerritory(defendingTerritory);

        if (!areSamePlayer(player, attacker)) {
            throw new GameplayException("Attacking player is not the same as player calling attack");
        }

        if (areSamePlayer(player, defender)) {
            throw new GameplayException("Attacker is same as defender");
        }

        attack(attacker, defender);

        AttackResult result = new AttackResult();
        result.attackTerritory = attacker.getName();
        result.defendTerritory = defender.getName();
        result.attackUnits = attacker.getUnits();
        result.defendUnits = defender.getUnits();
        return result;
    }

    //Assume all validation checks have already taken place
    public void attack(Territory attacker, Territory defender) {

        //Subtract 1 as needs to remain on territory
        int attackUnits = attacker.getAvailableUnits();
        int defendUnits = defender.getUnits();
        int originalAttackers = attackUnits;
        int originalDefenders = defendUnits;

        try {
            while (attackUnits > 0 && defendUnits > 0) {
                int toAttack = Math.min(attackUnits, 3);
                int toDefend = Math.min(defendUnits, 2);
                attackUnits -= toAttack;
                defendUnits -= toDefend;
                DiceManager.Result r = diceManager.engage(toAttack, toDefend);
                attackUnits += r.getAttackers();
                defendUnits += r.getDefenders();
            }
        } catch (GameplayException e) {
            e.printStackTrace();
        }

        attacker.subtractUnits(originalAttackers - attackUnits);
        defender.subtractUnits(originalDefenders - defendUnits);
    }

    public void endAttack(String playerName) throws PlayerNotFoundException {
        Player player = getPlayer(playerName);
        if (game.getCurrentPlayer().equals(player)) {
            game.nextState();
        }
    }

    public void fortify(String playerName, String fromTerritory, String toTerritory, int units) throws PlayerNotFoundException, TerritoryNotFoundException, GameplayException {
        if (game.getState() != FORTIFY) {
            throw new GameplayException("Not in fortify phase");
        }
        Player player = getPlayer(playerName);
        if (!player.equals(game.getCurrentPlayer())) {
            throw new GameplayException("Not current player");
        }

        Territory from = getTerritory(fromTerritory);
        Territory to = getTerritory(toTerritory);

        if (!areSamePlayer(player, from) || !areSamePlayer(player, to)) {
            return;
        }

        if (units > from.getAvailableUnits()) {
            throw new GameplayException("Moving too many units");
        }

        if (game.getBoard().areConnected(from, to)) {
            //Remove units from 'from' and add to 'to'
            from.subtractUnits(units);
            to.addUnits(units);
            endTurn(player);
        } else {
            throw new GameplayException("Territories not connected");
        }
    }

    @Override
    public GameState getGameState() {
        GameState gameState = new GameState(game.getCurrentPlayer().getName(), game.getState().toString());
        List<Territory> territories = game.getBoard().getTerritories();
        int sz = territories.size();
        gameState.territories = new String[sz];
        gameState.occupyingPlayers = new String[sz];
        gameState.units = new Integer[sz];
        gameState.unitsToPlace = getRemainingDraft(game.getCurrentPlayer());
        for (int i = 0; i < sz; i++) {
            Territory t = territories.get(i);
            gameState.territories[i] = t.getName();
            gameState.occupyingPlayers[i] = t.getPlayer().getName();
            gameState.units[i] = t.getUnits();
        }
        return gameState;
    }

    private boolean areSamePlayer(Player player, Territory territory) {
        return territory.getPlayer() != null && territory.getPlayer().equals(player);
    }

    void endTurn(Player player) {
        game.nextPlayer();
        game.nextState();
        Player nextPlayer = game.getCurrentPlayer();

        //Notify controller for this player
        PlayerOutput output = outputMap.get(nextPlayer);
        output.notifyTurn();
    }

    private Player getPlayer(String playerName) throws PlayerNotFoundException {
        Player player = game.getPlayer(playerName);
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        return player;
    }

    private Territory getTerritory(String territoryName) throws TerritoryNotFoundException {
        Territory territory = game.getBoard().getTerritory(territoryName);
        if (territory == null) {
            throw new TerritoryNotFoundException();
        }
        return territory;
    }

    public void start(ConsoleController controller) {
        while (!getGameState().hasEnded()) {
            controller.takeTurn(getGameState());
        }
    }

}
