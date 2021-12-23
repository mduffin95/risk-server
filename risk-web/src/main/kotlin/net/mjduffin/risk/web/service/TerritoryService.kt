package net.mjduffin.risk.web.service

interface TerritoryService {

    fun latest(): List<TerritoryVM>

    fun after(messageId: String): List<TerritoryVM>

    fun post(message: TerritoryVM)
}