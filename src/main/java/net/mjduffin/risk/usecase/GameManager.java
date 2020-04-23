package net.mjduffin.risk.usecase;

import net.mjduffin.risk.entities.Game;
import net.mjduffin.risk.entities.Player;
import net.mjduffin.risk.entities.Territory;

import java.util.HashMap;
import java.util.Map;

class GameManager implements PlayerInput {
    private Game game;

    Map<Player, PlayerOutput> outputMap = new HashMap<>();

    GameManager(Game game) {
        this.game = game;
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
        game.nextState();
    }


    public void attack(String playerName, String attackingTerritory, String defendingTerritory) {
        //Verify that it's player's go
        Player player = game.getPlayer(playerName);
        if (!player.equals(game.getCurrentPlayer())) {
            return;
        }

        Territory attacker = game.getBoard().getTerritory(attackingTerritory);
        Territory defending = game.getBoard().getTerritory(defendingTerritory);

        if (!areSamePlayer(player, attacker)) {
            //The player calling attack is not the same as the player occupying attacking territory
            //TODO: Throw exception
            return;
        }

        if (areSamePlayer(player, defending)) {
            //Attacker is same as defender
            return;
        }


    }

    public void endAttack(String playerName) {
        Player player = game.getPlayer(playerName);
        if (game.getCurrentPlayer().equals(player)) {
            game.nextState();
        }
    }

    public void fortify(String playerName, String fromTerritory, String toTerritory, int units) {
        Player player = game.getPlayer(playerName);
        if (!player.equals(game.getCurrentPlayer())) {
            return;
        }

        Territory from = game.getBoard().getTerritory(fromTerritory);
        Territory to = game.getBoard().getTerritory(toTerritory);

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

}
