package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.*
import net.mjduffin.risk.lib.usecase.request.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue


class GameManager internal constructor(private val board: Board, private var game: Game, private val diceManager: DiceManager) :
    PlayerInput,
    StateChangeObserver,
    PlayerChangeObserver,
    RequestAcceptor {

//    private val unitStores: Map<PlayerId, UnitStore> = HashMap()
    private val requestQueue: BlockingQueue<Request> = LinkedBlockingQueue()
    private var output: PlayerOutput? = null
    private var lastAttackingTerritory: TerritoryId? = null
    private var lastAttackingUnitCount = 0
    private var lastDefendingTerritory: TerritoryId? = null

    private fun isPlayerTurn(player: PlayerId): Boolean {
        return player == game.currentPlayer || game.state == Game.State.ALLDRAFT
    }

    //Returns true if a particular player has finished drafting
    private fun finishedDrafting(player: PlayerId): Boolean {
        return game.getDraftableUnits(player) == 0
    }

    private fun draftUnits(territory: TerritoryId, player: PlayerId, units: Int) {
        val remaining = game.getDraftableUnits(player)
        if (remaining > 0 && units <= remaining) {
            addUnitsToTerritory(territory, player, units)
        } else {
            throw GameplayException("Not enough units")
        }
    }

    private fun addUnitsToTerritory(territory: TerritoryId, player: PlayerId, units: Int) {
        val check = game.getPlayerForTerritory(territory)
        if (check == player) {
            game = game.addUnits(territory, units).useUnits(player, units)
        } else {
            throw GameplayException("Cannot add units to territory as it is not owned by the player")
        }
    }

    private fun areSamePlayer(player: PlayerId, territory: TerritoryId): Boolean {
        val p = game.getPlayerForTerritory(territory)
        return p == player
    }

    private fun processRequest(request: Request) {
        when (request) {
            is DraftRequest -> {
                draftSingle(request.player, request.territory, request.units)
            }
            is AttackRequest -> {
                attack(request.player, request.attacker, request.defender)
            }
            is MoveRequest -> {
                move(request.playerName, request.units)
            }
            is EndAttackRequest -> {
                endAttack(request.playerName)
            }
            is FortifyRequest -> {
                fortify(
                    request.playerName,
                    request.fromTerritory,
                    request.toTerritory,
                    request.units
                )
            }
            is SkipFortifyRequest -> endTurn()
        }
    }

    override fun registerPlayerOutput(output: PlayerOutput) {
        this.output = output
    }

    //Must be called with entire draft at once
    override fun draft(playerName: String, draft: Map<String, Int>) {
        //Verify that it's player's go (or ALL_DRAFT)
        val player = game.getPlayer(playerName)
        if (!isPlayerTurn(player)) {
            throw GameplayException("Not player's turn")
        }

        //Verify total number of troops is correct
        for ((territoryName, units) in draft) {
            val territoryId = TerritoryId(territoryName)
            draftUnits(territoryId, player, units)
        }
        if (game.state === Game.State.ALLDRAFT) {
            game = game.nextPlayer()
            if (game.isFirstPlayer) {
                game = game.nextState()
            }
        } else if (game.state === Game.State.DRAFT && finishedDrafting(player)) {
            game = game.nextState()
        }
    }

    override fun draftSingle(playerName: String, territoryName: String, units: Int) {
        //Verify that it's player's go (or ALL_DRAFT)
        val player = game.getPlayer(playerName)
        if (!isPlayerTurn(player)) {
            throw GameplayException("Not player's turn")
        }
        val territoryId = TerritoryId(territoryName)
        draftUnits(territoryId, player, units)
        if (game.state === Game.State.ALLDRAFT) {
            if (finishedDrafting(player)) {
                game = game.nextPlayer()
                if (game.isFirstPlayer) {
                    game = game.nextState()
                }
            }
        } else if (game.state === Game.State.DRAFT && finishedDrafting(player)) {
            game = game.nextState()
        }
    }

    override fun attack(playerName: String, attackingTerritory: String, defendingTerritory: String): AttackResult {
        //Verify that game is in attack phase it's player's go
        if (game.state !== Game.State.ATTACK) {
            throw GameplayException("Not in attack phase")
        }
        val attackingPlayer = game.getPlayer(playerName)
        if (attackingPlayer != game.currentPlayer) {
            throw GameplayException("Not current player")
        }
        val attacker = TerritoryId(attackingTerritory)
        val defender = TerritoryId(defendingTerritory)
        if (!areSamePlayer(attackingPlayer, attacker)) {
            throw GameplayException("Attacking player is not the same as player calling attack")
        }
        if (areSamePlayer(attackingPlayer, defender)) {
            throw GameplayException("Attacker is same as defender")
        }
        if (!board.areAdjacent(attacker, defender)) {
            throw GameplayException("Territories are not adjacent")
        }
        attack(attacker, defender)
        val result = AttackResult()
        result.attackTerritory = attacker.name
        result.defendTerritory = defender.name
        result.attackUnits = game.getUnits(attacker)
        result.defendUnits = game.getUnits(defender)
        if (result.defendUnits == 0) {
            //Attacker won, set new territory owner and transition to MOVE phase
            val defendingPlayer = game.getPlayerForTerritory(defender)
            game = game
                .setOwner(defendingPlayer, attackingPlayer, defender)
                .nextState()
        }
        return result
    }

    //Assume all validation checks have already taken place
    fun attack(attacker: TerritoryId, defender: TerritoryId) {
        lastAttackingTerritory = attacker
        lastDefendingTerritory = defender

        //Subtract 1 as needs to remain on territory
        var attackUnits: Int = game.getAvailableUnits(attacker)
        var defendUnits: Int = game.getUnits(defender)
        val originalAttackers = attackUnits
        val originalDefenders = defendUnits
        try {
            while (attackUnits > 0 && defendUnits > 0) {
                val toAttack = Math.min(attackUnits, 3)
                lastAttackingUnitCount = toAttack
                val toDefend = Math.min(defendUnits, 2)
                attackUnits -= toAttack
                defendUnits -= toDefend
                val r = diceManager.engage(toAttack, toDefend)
                attackUnits += r.attackers
                defendUnits += r.defenders
            }
        } catch (e: GameplayException) {
            e.printStackTrace()
        }
        game = game
            .addUnits(attacker, attackUnits - originalAttackers)
            .addUnits(defender, defendUnits - originalDefenders)
    }

    override fun endAttack(playerName: String) {
        val player = game.getPlayer(playerName)
        if (game.currentPlayer == player && game.state == Game.State.ATTACK) {
            game = game.goToState(Game.State.FORTIFY)
        }
    }

    override fun move(playerName: String, units: Int) {
        val p = game.getPlayer(playerName)
        if (units < lastAttackingUnitCount) {
            throw GameplayException("Move count must be >= $lastAttackingUnitCount")
        }
        val availableUnits = game.getAvailableUnits(lastAttackingTerritory!!)
        if (units > availableUnits) {
            throw GameplayException("Move count must be <= $availableUnits")
        }
        if (game.getPlayerForTerritory(lastAttackingTerritory!!) == p) {
            game = game.moveUnits(lastAttackingTerritory!!, lastDefendingTerritory!!, units)
        } else {
            throw GameplayException("Players are not the same")
        }
        game = game.nextState()
    }

    override fun fortify(playerName: String, fromTerritory: String, toTerritory: String, units: Int) {
        if (game.state !== Game.State.FORTIFY) {
            throw GameplayException("Not in fortify phase")
        }
        val player = game.getPlayer(playerName)
        if (player != game.currentPlayer) {
            throw GameplayException("Not current player")
        }
        val from = TerritoryId(fromTerritory)
        val to = TerritoryId(toTerritory)
        if (!areSamePlayer(player, from) || !areSamePlayer(player, to)) {
            throw GameplayException("Territory not owned by ${player.name}")
        }
        if (units > game.getAvailableUnits(from)) {
            throw GameplayException("Moving too many units")
        }
        if (board.areConnected(from, to, game::getPlayerForTerritory)) {
            //Remove units from 'from' and add to 'to'
            game = game.moveUnits(from, to, units)
            endTurn()
        } else {
            throw GameplayException("Territories not connected")
        }
    }

    override fun getGameState(): GameState {
        val territories: List<TerritoryId> = board.allTerritories().toList()
        val occupyingPlayers = territories.map { game.getPlayerForTerritory(it).name }
        val hasEnded = occupyingPlayers.distinct().size == 1
        val gameState = GameState(
            game.currentPlayer.name,
            game.state.toString(),
            game.currentDraftableUnits(),
            territories.map { it.name },
            occupyingPlayers,
            territories.map { game.getUnits(it) },
            hasEnded
        )
        return gameState
    }

    fun endTurn() {
        game = game.nextPlayer()
        game = game.nextState()
    }

    fun start() {
        var gameState = getGameState()
        while (!gameState.hasEnded) {
            output!!.turn(gameState)
            //TODO: wait to pull request off queue
            val r = requestQueue.take()
            try {
                processRequest(r)
            } catch (e: GameplayException) {
                System.err.println(e.message)
            }
            gameState = getGameState()
        }
        System.out.printf("Game has ended, %s has won!", gameState.occupyingPlayers[0])
    }

    override fun notify(oldPlayer: PlayerId, newPlayer: PlayerId) {
        // New player's go
    }

    override fun notify(oldState: Game.State, newState: Game.State) {
        println("New state: $newState")
    }

    override fun receiveRequest(request: Request) {
        requestQueue.add(request)
    }

    init {
        //Game is fully populated
//        game.registerPlayerChangeObserver(this)
//        game.registerStateChangeObserver(this)
    }
}