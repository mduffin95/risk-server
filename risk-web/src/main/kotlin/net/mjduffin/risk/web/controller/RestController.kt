package net.mjduffin.risk.web.controller

import net.mjduffin.risk.lib.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

val colors = listOf("red", "blue", "green", "violet", "orange", "magenta", "yellow")

@RestController
@RequestMapping("api")
class RestController(private val riskController: RiskController) {

    @PostMapping("/games/{gameId}/turn/draft")
    private fun draft(@PathVariable("gameId") gameId: String, @RequestBody draft: Draft): Response {
        return riskController.draft(gameId, draft)
    }

    @PostMapping("/games/{gameId}/turn/attack")
    private fun attack(@PathVariable("gameId") gameId: String, @RequestBody attack: Attack): Response {
        return riskController.attack(gameId, attack)
    }

    @PostMapping("/games/{gameId}/turn/move")
    fun move(@PathVariable("gameId") gameId: String, @RequestBody move: Move): Response {
        return riskController.move(gameId, move)
    }

    @PostMapping("/games/{gameId}/turn/end")
    private fun end(@PathVariable("gameId") gameId: String, @RequestBody endTurn: EndTurn): Response {
        return riskController.end(gameId, endTurn)
    }

    @PostMapping("/games/{gameId}/turn/fortify")
    private fun fortify(@PathVariable("gameId") gameId: String, @RequestBody fortify: Fortify): Response {
        return riskController.fortify(gameId, fortify)
    }

    @PostMapping("/games")
    private fun newGame(): String {
        return riskController.newGame()
    }

    @PutMapping("/games/{gameId}/players/{playerName}")
    fun join(@PathVariable("gameId") gameId: String, @PathVariable("playerName") playerName: String): Player {
        return riskController.join(gameId, playerName)
    }

    @GetMapping("/games/{gameId}/game/{count}")
    fun game(@PathVariable("gameId") gameId: String, @PathVariable("count") count: Int): ViewModel {
        return riskController.game(gameId, count);
    }

    @PostMapping("/games/{gameId}/start")
    fun start(@PathVariable("gameId") gameId: String): Response {
        return riskController.start(gameId)
    }

//    fun getViewModel(id: String): ViewModel =
//        containers[id]?.toViewModel() ?: ViewModel(Screen.ERROR, 0, "Container not found: $id")
}
