package net.mjduffin.risk.web.service

import net.mjduffin.risk.lib.GameContainer
import net.mjduffin.risk.lib.GameContainerService
import net.mjduffin.risk.lib.TerritoryService
import net.mjduffin.risk.lib.usecase.GameFactory
import net.mjduffin.risk.web.controller.LocalGameContainer
import org.apache.commons.lang3.RandomStringUtils

class MapBasedGameContainerService(
    private val gameFactory: GameFactory,
    private val territoryService: TerritoryService
) : GameContainerService {

    private val containers: MutableMap<String, GameContainer> = mutableMapOf()

    override fun createGame(): String {
        val id = RandomStringUtils.random(6, true, false)!!.uppercase()
        containers[id] = LocalGameContainer(gameFactory, territoryService)
        return id
    }

    override fun getContainer(gameId: String): GameContainer? {
        return containers[gameId]
    }
}