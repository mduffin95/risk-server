package net.mjduffin.risk.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RiskApplication

fun main(args: Array<String>) {
    runApplication<RiskApplication>(*args)
}