package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.web.service.TerritoryService
import net.mjduffin.risk.web.service.TerritoryVM
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class HtmlController(val messageService: TerritoryService, val gameManager: GameManager) {

    @GetMapping("/game")
    fun game(model: Model): String {
        val gameState = gameManager.getGameState();
        val messages: List<TerritoryVM> = messageService.convert(gameState)

        model["territories"] = messages


        return "risk"
    }

    @PostMapping("/game")
    fun lobby(): String {
        return "lobby"
    }

}