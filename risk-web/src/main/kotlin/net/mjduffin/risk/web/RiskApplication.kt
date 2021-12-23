package net.mjduffin.risk.web

import net.mjduffin.risk.lib.usecase.GameFactory
import net.mjduffin.risk.lib.usecase.GameManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
open class RiskApplication

fun main(args: Array<String>) {
    runApplication<RiskApplication>(*args)
}


@Configuration
open class GameConfiguration {
    @Bean
    open fun gameManager(): GameManager {
        val basicGame = GameFactory.mainGame()
//        basicGame.start()
        return basicGame
    }
}

