package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.web.service.TerritoryService
import net.mjduffin.risk.web.service.TerritoryVM
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class HtmlController(val messageService: TerritoryService, val gameManager: GameManager) {

    @GetMapping("/game")
    fun game(): List<TerritoryVM> {
        val gameState = gameManager.getGameState()
        return messageService.convert(gameState)
    }

    @PostMapping("/game")
    fun lobby(): String {
        return "lobby"
    }

}