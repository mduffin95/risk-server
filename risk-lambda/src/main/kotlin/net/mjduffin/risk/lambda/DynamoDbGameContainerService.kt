package net.mjduffin.risk.lambda

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import kotlinx.coroutines.runBlocking
import net.mjduffin.risk.lib.GameContainer
import net.mjduffin.risk.lib.GameContainerService
import org.apache.commons.lang3.RandomStringUtils

class DynamoDbGameContainerService : GameContainerService {

    private val tableName: String = System.getenv("TABLE_NAME") ?: "risk-lambda-sam-eu-west-1-471112856731"

    override fun createGame(): String {
        return runBlocking {
            println("Creating game")
            val id = putGame(tableName)
            id
        }
    }

    override fun getContainer(gameId: String): GameContainer? {
        TODO("Not yet implemented")
    }

    private suspend fun putGame(tableNameVal: String): String {

        val itemValues = mutableMapOf<String, AttributeValue>()
        val id = RandomStringUtils.random(6, true, false)!!.uppercase()
        // Add all content to the table.
        itemValues["id"] = AttributeValue.S(id)
        itemValues["actionCount"] = AttributeValue.N("0")
        itemValues["playerCount"] = AttributeValue.N("0")

        val request =
            PutItemRequest {
                tableName = tableNameVal
                item = itemValues
            }

        DynamoDbClient { region = "eu-west-1" }.use { ddb ->
            ddb.putItem(request)
            println("Added $id to the Movie table.")
        }
        return id
    }
}