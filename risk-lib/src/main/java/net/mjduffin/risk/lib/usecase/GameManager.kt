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

    private val unitStores: Map<Player, UnitStore> = HashMap()
    private val requestQueue: BlockingQueue<Request> = LinkedBlockingQueue()
    var output: PlayerOutput? = null
    var lastAttackingTerritory: TerritoryId? = null
    private var lastAttackingUnitCount = 0
    var lastDefendingTerritory: TerritoryId? = null

    override fun registerPlayerOutput(output: PlayerOutput) {
        this.output = output
    }

    private fun isPlayerTurn(player: Player): Boolean {
        return player.getId() == game.currentPlayer || game.state == Game.State.ALLDRAFT
    }

    //Returns true if a particular player has finished drafting
    private fun finishedDrafting(player: Player?): Boolean {
        return player!!.finishedDrafting()
    }

    private fun draftUnits(territory: TerritoryId, player: Player, units: Int) {
        val remaining = game.calculateDraftableUnits(player.getId())
        if (remaining > 0 && units <= remaining) {
            addUnitsToTerritory(territory, player, units)

            //Add new units on
            player.useUnits(units)
        } else {
            throw GameplayException("Not enough units")
        }
    }

    private fun addUnitsToTerritory(territory: TerritoryId, player: Player, units: Int) {
        val check = game.getPlayerForTerritory(territory)
        if (check != null && check == player.getId()) {
            game = game.addUnits(territory, units)
        } else {
            throw GameplayException("Cannot add units to territory as it is not owned by the player")
        }
    }

    //Must be called with entire draft at once
    override fun draft(playerName: String, draft: Map<String, Int>) {
        //Verify that it's player's go (or ALL_DRAFT)
        val player = getPlayer(playerName)
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
        val player = getPlayer(playerName)
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
        val attackingPlayer = getPlayer(playerName)
        if (attackingPlayer.getId() != game.currentPlayer) {
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
        attack(attacker, defender)
        val result = AttackResult()
        result.attackTerritory = attacker.name
        result.defendTerritory = defender.name
        result.attackUnits = game.getUnits(attacker)
        result.defendUnits = game.getUnits(defender)
        if (result.defendUnits == 0) {
            //Attacker won, set new territory owner and transition to MOVE phase
            val defendingPlayer = game.getPlayerForTerritory(defender)!!
            game = game
                .setOwner(defendingPlayer, attackingPlayer.getId(), defender)
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
        val player = getPlayer(playerName)
        if (game.currentPlayer == player.getId() && game.state == Game.State.ATTACK) {
            game = game.nextState()
        }
    }

    override fun move(playerName: String, units: Int) {
        val p = getPlayer(playerName)
        if (units < lastAttackingUnitCount) {
            throw GameplayException(String.format("Move count must be >= {}", lastAttackingUnitCount))
        }

        if (game.getPlayerForTerritory(lastAttackingTerritory!!)!! == p.getId()) {
            game = game.moveUnits(lastAttackingTerritory!!, lastDefendingTerritory!!, units)
        } else {
            throw GameplayException("Players are not the same")
        }
        game.nextState()
    }

    override fun fortify(playerName: String, fromTerritory: String, toTerritory: String, units: Int) {
        if (game.state !== Game.State.FORTIFY) {
            throw GameplayException("Not in fortify phase")
        }
        val player = getPlayer(playerName)
        if (player.getId() != game.currentPlayer) {
            throw GameplayException("Not current player")
        }
        val from = TerritoryId(fromTerritory)
        val to = TerritoryId(toTerritory)
        if (!areSamePlayer(player, from) || !areSamePlayer(player, to)) {
            return
        }
        if (units > game.getAvailableUnits(from)) {
            throw GameplayException("Moving too many units")
        }
        if (board.areConnected(from, to)) {
            //Remove units from 'from' and add to 'to'
            game = game.moveUnits(from, to, units)
            endTurn()
        } else {
            throw GameplayException("Territories not connected")
        }
    }

    override fun getGameState(): GameState {
        val territories: List<TerritoryId> = board.allTerritories().toList()
        val occupyingPlayers = territories.map { game.getPlayerForTerritory(it)!!.name }
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

    private fun areSamePlayer(player: Player, territory: TerritoryId): Boolean {
        val p = game.getPlayerForTerritory(territory)
        return p != null && p == player.getId()
    }

    fun endTurn() {
        game = game.nextPlayer()
        game = game.nextState()
    }

    private fun getPlayer(playerName: String): Player {
        return game.getPlayer(playerName)
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
        //New player's go

        //No harm doing this each time
//        newPlayer.calulateAndSetDraftableUnits(game.state)
    }

    override fun notify(oldState: Game.State, newState: Game.State) {
        println("New state: $newState")
    }

    override fun receiveRequest(request: Request) {
        requestQueue.add(request)
    }

    //TODO: Could pass requests directly into methods
    private fun processRequest(request: Request) {
        val type: Request.Type? = request.requestType
        when (type) {
            Request.Type.DRAFT -> {
                val draftRequest = request as DraftRequest
                draftSingle(draftRequest.player, draftRequest.territory, draftRequest.units)
            }
            Request.Type.ATTACK -> {
                val attackRequest = request as AttackRequest
                attack(attackRequest.player, attackRequest.attacker, attackRequest.defender)
            }
            Request.Type.MOVE -> {
                val moveRequest = request as MoveRequest
                move(moveRequest.playerName, moveRequest.units)
            }
            Request.Type.ENDATTACK -> {
                val req = request as EndAttackRequest
                endAttack(req.playerName)
            }
            Request.Type.FORTIFY -> {
                val fortifyRequest = request as FortifyRequest
                fortify(
                    fortifyRequest.playerName,
                    fortifyRequest.fromTerritory,
                    fortifyRequest.toTerritory,
                    fortifyRequest.units
                )
            }
            Request.Type.SKIPFORTIFY -> endTurn()
        }
    }

    init {
        //Game is fully populated
//        game.registerPlayerChangeObserver(this)
//        game.registerStateChangeObserver(this)
    }
}