package net.mjduffin.risk.usecase;

import net.mjduffin.risk.entities.*;

import java.util.HashMap;
import java.util.Map;

import static net.mjduffin.risk.entities.Game.State.*;

class GameManager implements PlayerInput {
    private Game game;
    private DiceManager diceManager;
    private int leftToDraft;

    Map<Player, PlayerOutput> outputMap = new HashMap<>();

    GameManager(Game game, DiceManager diceManager) {
        this.game = game;
        this.diceManager = diceManager;
        leftToDraft = game.getNumPlayers();
    }

    //Must be called with entire draft at once
    public void draft(String playerName, Map<String, Integer> draft) {
        //Verify that it's player's go
        Player player = game.getPlayer(playerName);
        if (!player.equals(game.getCurrentPlayer())) {
            return;
        }
        //Verify total number of troops is correct

        for (Map.Entry<String, Integer> e: draft.entrySet()) {
            String territoryName = e.getKey();
            Integer units = e.getValue();

            if (units != null) {
                game.getBoard().getTerritory(territoryName).addUnits(units);
            }
        }

        if (game.getState() == ALLDRAFT) {
            leftToDraft--;
            if (leftToDraft == 0) {
                game.nextState();
            }
        } else if (game.getState() == DRAFT) {
            game.nextState();
        }
    }


    public AttackResult attack(String playerName, String attackingTerritory, String defendingTerritory) throws PlayerNotFoundException, TerritoryNotFoundException, GameplayException {
        //Verify that it's player's go
        Player player = getPlayer(playerName);
        if (!player.equals(game.getCurrentPlayer())) {
            throw new GameplayException("Not current player");
        }

        Territory attacker = getTerritory(attackingTerritory);
        Territory defending = getTerritory(defendingTerritory);

        if (!areSamePlayer(player, attacker)) {
            throw new GameplayException("Attacking player is not the same as player calling attack");
        }

        if (areSamePlayer(player, defending)) {
            throw new GameplayException("Attacker is same as defender");
        }


        Engagement engagement = new Engagement(attacker, defending);
        engagement = attack(engagement);

        AttackResult result = new AttackResult();
        result.attackTerritory = engagement.attacker.getName();
        result.defendTerritory = engagement.defender.getName();
        result.attackUnits = engagement.attacker.getUnits();
        result.defendUnits = engagement.defender.getUnits();
        return result;
    }

    //Assume all validation checks have already taken place
    public Engagement attack(Engagement engagement) {
        Territory attacker = engagement.attacker;
        Territory defender = engagement.defender;

        //Subtract 1 as needs to remain on territory
        int attackUnits = attacker.getUnits() - 1;
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

        return engagement;
    }

    public void endAttack(String playerName) throws PlayerNotFoundException {
        Player player = getPlayer(playerName);
        if (game.getCurrentPlayer().equals(player)) {
            game.nextState();
        }
    }

    public void fortify(String playerName, String fromTerritory, String toTerritory, int units) throws PlayerNotFoundException, TerritoryNotFoundException {
        Player player = getPlayer(playerName);
        if (!player.equals(game.getCurrentPlayer())) {
            return;
        }

        Territory from = getTerritory(fromTerritory);
        Territory to = getTerritory(toTerritory);

        if (!areSamePlayer(player, from) || !areSamePlayer(player, to)) {
            return;
        }

        if (units > (from.getUnits() - 1)) {
            //Moving too many units
            return;
        }

        if (game.getBoard().areConnected(from, to)) {
            //Remove units from 'from' and add to 'to'
            from.subtractUnits(units);
            to.addUnits(units);
            endTurn(player);
        } else {
            //TODO: Throw exception
            return;
        }

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

}
