package net.mjduffin.risk.web.service

import org.springframework.stereotype.Service

@Service
class FakeTerritoryService : TerritoryService {

    override fun latest(): List<TerritoryVM> {
        return listOf(
            // north america
            TerritoryVM("alaska", 12, 6),
            TerritoryVM("nw-territory", 12, 15),
            TerritoryVM("greenland", 9, 33),
            TerritoryVM("alberta", 20, 15),
            TerritoryVM("ontario", 21, 21),
            TerritoryVM("quebec", 20, 28),
            TerritoryVM("western-us", 32, 15),
            TerritoryVM("eastern-us", 32, 23),
            TerritoryVM("central", 44, 15),
            // south america
            TerritoryVM("venezuela", 52, 22),
            TerritoryVM("brazil", 60, 30),
            TerritoryVM("peru", 60, 20),
            TerritoryVM("argentina", 75, 24),
            // europe
            TerritoryVM("great-britain", 28, 40),
            TerritoryVM("iceland", 18, 41),
            TerritoryVM("scandanavia", 18, 48),
            TerritoryVM("northern-europe", 30, 48),
            TerritoryVM("southern-europe", 40, 49),
            TerritoryVM("western-europe", 39, 42),
            TerritoryVM("ukraine", 24, 57),
            // africa
            TerritoryVM("north-africa", 56, 44),
            TerritoryVM("egypt", 53, 52),
            TerritoryVM("east-africa", 62, 56),
            TerritoryVM("congo", 69, 52),
            TerritoryVM("south-africa", 84, 53),
            TerritoryVM("madagascar", 82, 61),
            // asia
            TerritoryVM("ural", 23, 67),
            TerritoryVM("siberia", 16, 72),
            TerritoryVM("yakutsk", 11, 79),
            TerritoryVM("kamchatka", 11, 87),
            TerritoryVM("irkutsk", 23, 78),
            TerritoryVM("mongolia", 32, 79),
            TerritoryVM("japan", 34, 89),
            TerritoryVM("afghanistan", 35, 66),
            TerritoryVM("china", 41, 78),
            TerritoryVM("india", 49, 71),
            TerritoryVM("middle-east", 45, 59),
            TerritoryVM("siam", 53, 79),
            // australasia
            TerritoryVM("indonesia", 68, 80),
            TerritoryVM("new-guinea", 64, 89),
            TerritoryVM("western-aus", 80, 84),
            TerritoryVM("eastern-aus", 80, 93),
        )
//        val count = Random.nextInt(1, 15)
//        return (0..count).map {
//            val user = users.values.random()
//            val userQuote = usersQuotes.getValue(user.name).invoke()
//
//            TerritoryVM(userQuote, user, Instant.now(), Random.nextBytes(10).toString())
//        }.toList()
    }

    override fun after(messageId: String): List<TerritoryVM> {
        return latest()
    }

    override fun post(message: TerritoryVM) {
        TODO("Not yet implemented")
    }
}
