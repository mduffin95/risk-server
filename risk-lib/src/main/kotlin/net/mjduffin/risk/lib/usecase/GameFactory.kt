package net.mjduffin.risk.lib.usecase

import net.mjduffin.risk.lib.entities.*

object GameFactory {
    private const val NORTH_AMERICA = "NorthAmerica"
    private const val SOUTH_AMERICA = "SouthAmerica"
    private const val EUROPE = "Europe"
    private const val AFRICA = "Africa"
    private const val ASIA = "Asia"
    private const val AUSTRALASIA = "Australasia"

    private const val ALASKA = "alaska"
    private const val NW_TERRITORY = "nw-territory"
    private const val GREENLAND = "greenland"
    private const val ALBERTA = "alberta"
    private const val ONTARIO = "ontario"
    private const val QUEBEC = "quebec"
    private const val WESTERN_US = "western-us"
    private const val EASTERN_US = "eastern_us"
    private const val CENTRAL = "central"
    private const val VENEZUELA = "venezuela"
    private const val BRAZIL = "brazil"
    private const val PERU = "peru"
    private const val ARGENTINA = "argentina"
    private const val GREAT_BRITAIN = "great-britain"
    private const val ICELAND = "iceland"
    private const val SCANDANAVIA = "scandanavia"
    private const val NORTHERN = "northern-europe"
    private const val SOUTHERN = "soutern-europe"
    private const val WESTERN_EUROPE = "western-europe"
    private const val UKRAINE = "ukraine"
    private const val EGYPT = "egypt"
    private const val EAST = "east-africa"
    private const val CONGO = "congo"
    private const val SOUTH = "south-africa"
    private const val MADAGASCAR = "madagascar"
    private const val SIBERIA = "siberia"
    private const val YAKUTSK = "yakutsk"
    private const val KAMCHATKA = "kamchatka"
    private const val IRKUTSK = "irkutsk"
    private const val MONGOLIA = "mongolia"
    private const val JAPAN = "japan"
    private const val AFGHANISTAN = "afghanistan"
    private const val CHINA = "china"
    private const val INDIA = "india"
    private const val MIDDLE_EAST = "middle-east"
    private const val SIAM = "siam"
    private const val INDONESIA = "indonesia"
    private const val NEW_GUINEA = "new-guinea"
    private const val WESTERN_AUS = "western-aus"
    private const val EASTERN_AUS = "eastern-aus"

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

    @JvmStatic
    fun mainGame(): GameManager {
        val gameBuilder = Game.Builder()
        gameBuilder.addPlayerWithTerritories("Alice", listOf(INDONESIA, NEW_GUINEA))
        gameBuilder.addPlayerWithTerritories("Bob", listOf(WESTERN_AUS, EASTERN_AUS))
        val australasia = Continent("australasia", 2)
        val board = Board.Builder()
            .addEdge(INDONESIA, NEW_GUINEA)
            .addEdge(INDONESIA, WESTERN_AUS)
            .addEdge(WESTERN_AUS, EASTERN_AUS)
            .addEdge(WESTERN_AUS, NEW_GUINEA)
            .addEdge(EASTERN_AUS, NEW_GUINEA)
            .addToContinent(australasia, INDONESIA)
            .addToContinent(australasia, NEW_GUINEA)
            .addToContinent(australasia, WESTERN_AUS)
            .addToContinent(australasia, EASTERN_AUS)
            .build()
        val game = gameBuilder.build()
        val dieThrow: DieThrow = RandomDieThrow()
        val diceManager = DiceManager(dieThrow)
        return GameManager(board, game, diceManager)
    }
}