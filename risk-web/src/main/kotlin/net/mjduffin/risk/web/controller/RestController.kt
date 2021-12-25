package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.usecase.GameManager
import net.mjduffin.risk.web.service.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class RestController(val messageService: TerritoryService, val gameManager: GameManager) {

    @GetMapping("/game")
    fun game(): GameVM {
        val gameState = gameManager.getGameState()
        return messageService.convert(gameState)
    }

    @PostMapping("/draft")
    fun draft(@RequestBody draft: Draft): GameVM {
        val currentState = gameManager.getGameState()
        try {
            gameManager.draftSingle(currentState.currentPlayer, draft.territory, 1)
        } catch (ex: Exception) {
            return messageService.convert(currentState, ex.message)
        }
        return messageService.convert(gameManager.getGameState())
    }

    @PostMapping("/attack")
    fun attack(@RequestBody attack: Attack): GameVM {
        val currentState = gameManager.getGameState()
        try {
            gameManager.attack(currentState.currentPlayer, attack.from, attack.to)
        } catch (ex: Exception) {
            return messageService.convert(currentState, ex.message)
        }
        return messageService.convert(gameManager.getGameState())
    }

    @PostMapping("/move")
    fun move(@RequestBody move: Move): GameVM {
        val currentState = gameManager.getGameState()
        try {
            gameManager.move(currentState.currentPlayer, move.units)
        } catch (ex: Exception) {
            return messageService.convert(currentState, ex.message)
        }
        return messageService.convert(gameManager.getGameState())
    }

}