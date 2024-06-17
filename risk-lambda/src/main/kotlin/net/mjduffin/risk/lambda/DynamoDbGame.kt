package net.mjduffin.risk.lambda

import net.mjduffin.risk.lib.entities.Game
import net.mjduffin.risk.lib.entities.PlayerId
import net.mjduffin.risk.lib.entities.TerritoryId
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

/**
 * Properties must be mutable for the mapper to work.
 * <p>
 * Also requires a no-args construtor.
 */
@DynamoDbBean
data class DynamoDbGame (

    @get:DynamoDbPartitionKey var id: String? = null,

    var players: List<PlayerId> = emptyList(),
    var playerTerritories: Map<PlayerId, List<TerritoryId>> = emptyMap(),
    var territoryUnits: Map<TerritoryId, Int> = emptyMap(),
    var currentPlayer: PlayerId? = null,
    var state: Game.State = Game.State.ALLDRAFT,
    var draftRemaining: Map<PlayerId, Int> = emptyMap()
)