package net.mjduffin.risk.lambda

import net.mjduffin.risk.lib.GameContainer
import net.mjduffin.risk.lib.GameContainerService

class DynamoDbGameContainerService : GameContainerService {
    override fun createGame(): String {
        TODO("Not yet implemented")
    }

    override fun getContainer(gameId: String): GameContainer? {
        TODO("Not yet implemented")
    }
}