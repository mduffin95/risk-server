package net.mjduffin.risk.lib.usecase;

import net.mjduffin.risk.lib.entities.*;
import net.mjduffin.risk.lib.usecase.request.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static net.mjduffin.risk.lib.entities.Game.State.*;

public class GameManager implements PlayerInput, StateChangeObserver, PlayerChangeObserver, RequestAcceptor {
    private Game game;
    private DiceManager diceManager;
    private Map<Player, UnitStore> unitStores = new HashMap<>();
    private BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();

    PlayerOutput output;
    Territory lastAttackingTerritory;
    private int lastAttackingUnitCount;
    Territory lastDefendingTerritory;

    GameManager(Game game, DiceManager diceManager) {
        this.game = game; //Game is fully populated
        this.diceManager = diceManager;

        game.registerPlayerChangeObserver(this);
        game.registerStateChangeObserver(this);
        game.getCurrentPlayer().calulateAndSetDraftableUnits(game.getState());
    }

    @Override
    public void registerPlayerOutput(PlayerOutput output) {
        this.output = output;
    }

    private boolean isPlayerTurn(Player player) {
        if (player.equals(game.getCurrentPlayer()) || game.getState().equals(ALLDRAFT)) {
            return true;
        } else {
            return false;
        }
    }

    //Returns true if a particular player has finished drafting
    private boolean finishedDrafting(Player player) {
        return player.finishedDrafting();
    }

    private void draftUnits(Territory territory, Player player, int units) throws GameplayException {

        int remaining = player.getDraftableUnits();

        if (remaining > 0 && units <= remaining) {
            addUnitsToTerritory(territory, player, units);

            //Add new units on
            player.useUnits(units);
        } else {
            throw new GameplayException("Not enough units");
        }
    }

    private void addUnitsToTerritory(Territory territory, Player player, int units) throws GameplayException {
        if (territory.player.equals(player)) {
            territory.addUnits(units);
        } else {
            throw new GameplayException("Cannot add units to territory as it is not owned by the player");
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
            if (game.isFirstPlayer()) {
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
                game.nextPlayer();
                if (game.isFirstPlayer()) {
                    game.nextState();
                }
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

        if (result.defendUnits == 0) {
            //Attacker won, set new territory owner and transition to MOVE phase
            defender.player = player;
            game.nextState();
        }

        return result;
    }

    //Assume all validation checks have already taken place
    public void attack(Territory attacker, Territory defender) {
        lastAttackingTerritory = attacker;
        lastDefendingTerritory = defender;

        //Subtract 1 as needs to remain on territory
        int attackUnits = attacker.getAvailableUnits();
        int defendUnits = defender.getUnits();
        int originalAttackers = attackUnits;
        int originalDefenders = defendUnits;

        try {
            while (attackUnits > 0 && defendUnits > 0) {
                int toAttack = Math.min(attackUnits, 3);
                lastAttackingUnitCount = toAttack;
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
//            game.nextState();
            game.setState(FORTIFY);
        }
    }

    @Override
    public void move(String playerName, int units) throws PlayerNotFoundException, GameplayException {
        Player p = getPlayer(playerName);

        if (units < lastAttackingUnitCount) {
            throw new GameplayException(String.format("Move count must be >= {}", lastAttackingUnitCount));
        }

        if (lastAttackingTerritory.player.equals(p)) {
            lastAttackingTerritory.subtractUnits(units);
            lastDefendingTerritory.addUnits(units);
        } else {
            throw new GameplayException("Players are not the same");
        }

        game.nextState();
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
            endTurn();
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
        gameState.unitsToPlace = game.getCurrentPlayer().getDraftableUnits();
        for (int i = 0; i < sz; i++) {
            Territory t = territories.get(i);
            gameState.territories[i] = t.getName();
            gameState.occupyingPlayers[i] = t.player.getName();
            gameState.units[i] = t.getUnits();
        }
        return gameState;
    }

    private boolean areSamePlayer(Player player, Territory territory) {
        return territory.player != null && territory.player.equals(player);
    }

    void endTurn() {
        game.nextPlayer();
        game.nextState();
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

    public void start() throws InterruptedException, PlayerNotFoundException, TerritoryNotFoundException {
        while (!getGameState().hasEnded()) {
            output.turn(getGameState());
            //TODO: wait to pull request off queue
            Request r = requestQueue.take();
            try {
                processRequest(r);
            } catch (GameplayException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void notify(Player oldPlayer, Player newPlayer) {
        //New player's go

        //No harm doing this each time
        newPlayer.calulateAndSetDraftableUnits(game.getState());
    }

    @Override
    public void notify(Game.State oldState, Game.State newState) {
        System.out.println("New state: " + newState.toString());
    }

    @Override
    public void receiveRequest(Request request) {
        requestQueue.add(request);
    }

    //TODO: Could pass requests directly into methods
    private void processRequest(Request request) throws GameplayException, TerritoryNotFoundException, PlayerNotFoundException {
        Request.Type type = request.getRequestType();
        switch (type) {
            case DRAFT:
                DraftRequest draftRequest = (DraftRequest) request;
                draftSingle(draftRequest.getPlayer(), draftRequest.getTerritory(), draftRequest.getUnits());
                break;
            case ATTACK:
                AttackRequest attackRequest = (AttackRequest) request;
                attack(attackRequest.getPlayer(), attackRequest.getAttacker(), attackRequest.getDefender());
                break;
            case MOVE:
                MoveRequest moveRequest = (MoveRequest) request;
                move(moveRequest.getPlayerName(), moveRequest.getUnits());
                break;
            case ENDATTACK:
                EndAttackRequest req = (EndAttackRequest) request;
                endAttack(req.getPlayerName());
                break;
            case FORTIFY:
                FortifyRequest fortifyRequest = (FortifyRequest) request;
                fortify(
                        fortifyRequest.getPlayerName(),
                        fortifyRequest.getFromTerritory(),
                        fortifyRequest.getToTerritory(),
                        fortifyRequest.getUnits());
                break;
            case SKIPFORTIFY:
                endTurn();
        }


    }
}
