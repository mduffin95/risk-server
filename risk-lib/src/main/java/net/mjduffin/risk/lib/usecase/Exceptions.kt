package net.mjduffin.risk.lib.usecase

class TerritoryNotFoundException : Exception()

class PlayerNotFoundException : Exception()

class GameplayException(message: String?) : java.lang.Exception(message)
