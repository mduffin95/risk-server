package net.mjduffin.risk.web.service

import net.mjduffin.risk.lib.GameVM
import org.springframework.stereotype.Service

@Service
class TerritoryService {
    private val positionsMap = mapOf(
        // north america
        Pair("alaska", Pair(12, 6)),
        Pair("nw-territory", Pair(12, 15)),
        Pair("greenland", Pair(9, 33)),
        Pair("alberta", Pair(20, 15)),
        Pair("ontario", Pair(21, 21)),
        Pair("quebec", Pair(20, 28)),
        Pair("western-us", Pair(32, 15)),
        Pair("eastern-us", Pair(32, 23)),
        Pair("central", Pair(44, 15)),
        // south america
        Pair("venezuela", Pair(52, 22)),
        Pair("brazil", Pair(60, 30)),
        Pair("peru", Pair(60, 20)),
        Pair("argentina", Pair(75, 24)),
        // europe
        Pair("great-britain", Pair(28, 40)),
        Pair("iceland", Pair(18, 41)),
        Pair("scandanavia", Pair(18, 48)),
        Pair("northern-europe", Pair(30, 48)),
        Pair("southern-europe", Pair(40, 49)),
        Pair("western-europe", Pair(39, 42)),
        Pair("ukraine", Pair(24, 57)),
        // africa
        Pair("north-africa", Pair(56, 44)),
        Pair("egypt", Pair(53, 52)),
        Pair("east-africa", Pair(62, 56)),
        Pair("congo", Pair(69, 52)),
        Pair("south-africa", Pair(84, 53)),
        Pair("madagascar", Pair(82, 61)),
        // asia
        Pair("ural", Pair(23, 67)),
        Pair("siberia", Pair(16, 72)),
        Pair("yakutsk", Pair(11, 79)),
        Pair("kamchatka", Pair(11, 87)),
        Pair("irkutsk", Pair(23, 78)),
        Pair("mongolia", Pair(32, 79)),
        Pair("japan", Pair(34, 89)),
        Pair("afghanistan", Pair(35, 66)),
        Pair("china", Pair(41, 78)),
        Pair("india", Pair(49, 71)),
        Pair("middle-east", Pair(45, 59)),
        Pair("siam", Pair(53, 79)),
        // australasia
        Pair("indonesia", Pair(68, 80)),
        Pair("new-guinea", Pair(64, 89)),
        Pair("western-aus", Pair(80, 84)),
        Pair("eastern-aus", Pair(80, 93)),
    )


    fun getPosition(territory: String): Pair<Int, Int> {
        return positionsMap[territory] ?: throw IllegalArgumentException("Missing $territory position")
    }

    fun error(errorMessage: String): GameVM {
        return GameVM("", "", 0, listOf(), null, errorMessage)
    }
}
