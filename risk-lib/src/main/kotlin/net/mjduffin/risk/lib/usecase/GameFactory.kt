package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.*

object GameFactory {
    private const val NORTH_AMERICA = "NorthAmerica"
    private const val SOUTH_AMERICA = "SouthAmerica"
    private const val EUROPE = "Europe"
    private const val AFRICA = "Africa"
    private const val ASIA = "Asia"
    private const val AUSTRALASIA = "Australasia"

    @JvmStatic
    fun basicGame(): GameManager {
        val gameBuilder = Game.Builder()
        val terr1: MutableList<String> = ArrayList()
        terr1.add(NORTH_AMERICA)
        terr1.add(SOUTH_AMERICA)
        terr1.add(EUROPE)
        val terr2: MutableList<String> = ArrayList()
        terr2.add(AFRICA)
        terr2.add(ASIA)
        terr2.add(AUSTRALASIA)
        gameBuilder.addPlayerWithTerritories("Alice", terr1)
        gameBuilder.addPlayerWithTerritories("Bob", terr2)
        val c1 = Continent("C1", 5)
        val c2 = Continent("C2", 3)
        val c3 = Continent("C3", 1)
        val board = Board.Builder().addEdge(NORTH_AMERICA, SOUTH_AMERICA)
            .addEdge(NORTH_AMERICA, EUROPE)
            .addEdge(NORTH_AMERICA, ASIA)
            .addEdge(SOUTH_AMERICA, AFRICA)
            .addEdge(EUROPE, AFRICA)
            .addEdge(EUROPE, ASIA)
            .addEdge(ASIA, AUSTRALASIA)
            .addToContinent(c1, NORTH_AMERICA)
            .addToContinent(c1, SOUTH_AMERICA)
            .addToContinent(c2, ASIA)
            .addToContinent(c2, AUSTRALASIA)
            .addToContinent(c3, EUROPE)
            .addToContinent(c3, AFRICA)
            .build()
        val game = gameBuilder.build()
        val dieThrow: DieThrow = RandomDieThrow()
        val diceManager = DiceManager(dieThrow)
        return GameManager(board, game, diceManager)
    }
}