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
    private const val EASTERN_US = "eastern-us"
    private const val CENTRAL_AMERICA = "central"
    private const val VENEZUELA = "venezuela"
    private const val BRAZIL = "brazil"
    private const val PERU = "peru"
    private const val ARGENTINA = "argentina"
    private const val GREAT_BRITAIN = "great-britain"
    private const val ICELAND = "iceland"
    private const val SCANDANAVIA = "scandanavia"
    private const val NORTHERN_EUROPE = "northern-europe"
    private const val SOUTHERN_EUROPE = "southern-europe"
    private const val WESTERN_EUROPE = "western-europe"
    private const val UKRAINE = "ukraine"
    private const val NORTH_AFRICA = "north-africa"
    private const val EGYPT = "egypt"
    private const val EAST_AFRICA = "east-africa"
    private const val CONGO = "congo"
    private const val SOUTH_AFRICA = "south-africa"
    private const val MADAGASCAR = "madagascar"
    private const val URAL = "ural"
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
        val board = Board.Builder()
            .addEdge(NORTH_AMERICA, SOUTH_AMERICA)
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
    fun mainGame() = mainGame(listOf("Liz", "Rachel", "Matt"))

    @JvmStatic
    fun mainGame(originalPlayers: List<String>): GameManager {
        val gameBuilder = Game.Builder()
        val players = originalPlayers.toMutableList()
        players.shuffle()
        val australasia = Continent("australasia", 2)
        val southAmerica = Continent("south-america", 2)
        val africa = Continent("africa", 3)
        val europe = Continent("europe", 5)
        val northAmerica = Continent("north-america", 5)
        val asia = Continent("asia", 7)
        val board = Board.Builder()
            .addEdge(INDONESIA, NEW_GUINEA)
            .addEdge(INDONESIA, WESTERN_AUS)
            .addEdge(WESTERN_AUS, EASTERN_AUS)
            .addEdge(WESTERN_AUS, NEW_GUINEA)
            .addEdge(EASTERN_AUS, NEW_GUINEA)
            .addEdge(INDONESIA, SIAM)

            .addEdge(SIAM, CHINA)
            .addEdge(SIAM, INDIA)
            .addEdge(CHINA, INDIA)
            .addEdge(MIDDLE_EAST, INDIA)
            .addEdge(MIDDLE_EAST, AFGHANISTAN)
            .addEdge(INDIA, AFGHANISTAN)
            .addEdge(CHINA, AFGHANISTAN)
            .addEdge(CHINA, URAL)
            .addEdge(CHINA, SIBERIA)
            .addEdge(CHINA, MONGOLIA)
            .addEdge(AFGHANISTAN, URAL)
            .addEdge(SIBERIA, URAL)
            .addEdge(SIBERIA, MONGOLIA)
            .addEdge(IRKUTSK, MONGOLIA)
            .addEdge(JAPAN, MONGOLIA)
            .addEdge(JAPAN, KAMCHATKA)
            .addEdge(KAMCHATKA, IRKUTSK)
            .addEdge(KAMCHATKA, YAKUTSK)
            .addEdge(KAMCHATKA, MONGOLIA)
            .addEdge(SIBERIA, YAKUTSK)
            .addEdge(IRKUTSK, YAKUTSK)
            .addEdge(IRKUTSK, SIBERIA)

            .addEdge(UKRAINE, URAL)
            .addEdge(UKRAINE, AFGHANISTAN)
            .addEdge(UKRAINE, MIDDLE_EAST)
            .addEdge(SOUTHERN_EUROPE, MIDDLE_EAST)
            .addEdge(SOUTHERN_EUROPE, EGYPT)
            .addEdge(SOUTHERN_EUROPE, NORTH_AFRICA)
            .addEdge(WESTERN_EUROPE, NORTH_AFRICA)
            .addEdge(WESTERN_EUROPE, GREAT_BRITAIN)
            .addEdge(WESTERN_EUROPE, SOUTHERN_EUROPE)
            .addEdge(WESTERN_EUROPE, NORTHERN_EUROPE)
            .addEdge(SOUTHERN_EUROPE, NORTHERN_EUROPE)
            .addEdge(SOUTHERN_EUROPE, UKRAINE)
            .addEdge(NORTHERN_EUROPE, UKRAINE)
            .addEdge(NORTHERN_EUROPE, SCANDANAVIA)
            .addEdge(NORTHERN_EUROPE, GREAT_BRITAIN)
            .addEdge(GREAT_BRITAIN, ICELAND)
            .addEdge(GREAT_BRITAIN, SCANDANAVIA)
            .addEdge(SCANDANAVIA, UKRAINE)
            .addEdge(SCANDANAVIA, ICELAND)

            .addEdge(EAST_AFRICA, MIDDLE_EAST)
            .addEdge(NORTH_AFRICA, EGYPT)
            .addEdge(EAST_AFRICA, EGYPT)
            .addEdge(MIDDLE_EAST, EGYPT)
            .addEdge(EAST_AFRICA, NORTH_AFRICA)
            .addEdge(NORTH_AFRICA, CONGO)
            .addEdge(EAST_AFRICA, CONGO)
            .addEdge(EAST_AFRICA, SOUTH_AFRICA)
            .addEdge(CONGO, SOUTH_AFRICA)
            .addEdge(EAST_AFRICA, MADAGASCAR)
            .addEdge(SOUTH_AFRICA, MADAGASCAR)
            .addEdge(NORTH_AFRICA, BRAZIL)

            .addEdge(VENEZUELA, BRAZIL)
            .addEdge(PERU, BRAZIL)
            .addEdge(ARGENTINA, BRAZIL)
            .addEdge(ARGENTINA, PERU)
            .addEdge(VENEZUELA, PERU)
            .addEdge(VENEZUELA, CENTRAL_AMERICA)

            .addEdge(WESTERN_US, CENTRAL_AMERICA)
            .addEdge(EASTERN_US, CENTRAL_AMERICA)
            .addEdge(WESTERN_US, EASTERN_US)
            .addEdge(WESTERN_US, ALBERTA)
            .addEdge(WESTERN_US, ONTARIO)
            .addEdge(EASTERN_US, ONTARIO)
            .addEdge(EASTERN_US, QUEBEC)
            .addEdge(GREENLAND, QUEBEC)
            .addEdge(GREENLAND, ONTARIO)
            .addEdge(GREENLAND, NW_TERRITORY)
            .addEdge(GREENLAND, ICELAND)
            .addEdge(ALASKA, ALBERTA)
            .addEdge(NW_TERRITORY, ALBERTA)
            .addEdge(ONTARIO, ALBERTA)
            .addEdge(ONTARIO, QUEBEC)
            .addEdge(ONTARIO, NW_TERRITORY)
            .addEdge(NW_TERRITORY, ALASKA)
            .addEdge(KAMCHATKA, ALASKA)

            .addToContinent(australasia, INDONESIA)
            .addToContinent(australasia, NEW_GUINEA)
            .addToContinent(australasia, WESTERN_AUS)
            .addToContinent(australasia, EASTERN_AUS)
            .addToContinent(asia, SIAM)
            .addToContinent(asia, INDIA)
            .addToContinent(asia, MIDDLE_EAST)
            .addToContinent(asia, AFGHANISTAN)
            .addToContinent(asia, CHINA)
            .addToContinent(asia, URAL)
            .addToContinent(asia, SIBERIA)
            .addToContinent(asia, IRKUTSK)
            .addToContinent(asia, YAKUTSK)
            .addToContinent(asia, KAMCHATKA)
            .addToContinent(asia, JAPAN)
            .addToContinent(asia, MONGOLIA)

            .addToContinent(europe, UKRAINE)
            .addToContinent(europe, SOUTHERN_EUROPE)
            .addToContinent(europe, WESTERN_EUROPE)
            .addToContinent(europe, NORTHERN_EUROPE)
            .addToContinent(europe, GREAT_BRITAIN)
            .addToContinent(europe, SCANDANAVIA)
            .addToContinent(europe, ICELAND)

            .addToContinent(africa, NORTH_AFRICA)
            .addToContinent(africa, EGYPT)
            .addToContinent(africa, EAST_AFRICA)
            .addToContinent(africa, CONGO)
            .addToContinent(africa, SOUTH_AFRICA)
            .addToContinent(africa, MADAGASCAR)

            .addToContinent(southAmerica, ARGENTINA)
            .addToContinent(southAmerica, PERU)
            .addToContinent(southAmerica, BRAZIL)
            .addToContinent(southAmerica, VENEZUELA)

            .addToContinent(northAmerica, CENTRAL_AMERICA)
            .addToContinent(northAmerica, EASTERN_US)
            .addToContinent(northAmerica, WESTERN_US)
            .addToContinent(northAmerica, ALBERTA)
            .addToContinent(northAmerica, ONTARIO)
            .addToContinent(northAmerica, QUEBEC)
            .addToContinent(northAmerica, ALASKA)
            .addToContinent(northAmerica, NW_TERRITORY)
            .addToContinent(northAmerica, GREENLAND)

            .build()

        val territories = board.allTerritories().map { it.name }.toMutableList()
        territories.shuffle()
        val pmap = mutableMapOf<String, List<String>>()
        var count = 0
        while (territories.isNotEmpty()) {
            val t = territories.removeFirst()
            val playerIndex = count % players.size
            val player = players[playerIndex]

            val current = pmap[player] ?: listOf()
            pmap[player] = current + t
            count++
        }

        for ((k, v) in pmap) {
            gameBuilder.addPlayerWithTerritories(k, v)
        }

        val game = gameBuilder.build()
        val dieThrow: DieThrow = RandomDieThrow()
        val diceManager = DiceManager(dieThrow)
        return GameManager(board, game, diceManager)
    }
}