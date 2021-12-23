package net.mjduffin.risk.web.controller

import net.mjduffin.risk.web.service.TerritoryService
import net.mjduffin.risk.web.service.TerritoryVM
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController(val messageService: TerritoryService) {

    @GetMapping("/")
    fun index(model: Model): String {
        val messages: List<TerritoryVM> = messageService.latest()

        model["territories"] = messages
//        model["lastMessageId"] = messages.lastOrNull()?.id ?: ""

        return "risk"
    }
}