package net.mjduffin.risk.web

import net.mjduffin.risk.lib.usecase.GameFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
open class RiskApplication

fun main(args: Array<String>) {
    runApplication<RiskApplication>(*args)
}


@Configuration
open class GameConfiguration {
    @Bean
    open fun gameManager(): GameFactory {
        return GameFactory
    }

    @Value("\${cors.originPatterns:default}")
    open val corsOriginPatterns: String = ""

    @Bean
    open fun addCorsConfig(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                val allowedOrigins = corsOriginPatterns.split(",").toTypedArray()
                registry.addMapping("/**")
                    .allowedMethods("*")
                    .allowedOriginPatterns(*allowedOrigins)
                    .allowCredentials(true)
            }
        }
    }
}
