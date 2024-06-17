package net.mjduffin.risk.lambda

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import kotlinx.coroutines.runBlocking
import net.mjduffin.risk.lib.GameContainer
import net.mjduffin.risk.lib.GameContainerService
import net.mjduffin.risk.lib.Player
import net.mjduffin.risk.lib.ViewModel
import net.mjduffin.risk.lib.usecase.GameManager
import org.apache.commons.lang3.RandomStringUtils
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class DynamoDbGameContainerService : GameContainerService {
    var gameTableSchema : TableSchema<DynamoDbGame> = TableSchema.fromBean(DynamoDbGame::class.java);

    private val tableName: String = System.getenv("TABLE_NAME") ?: "risk-lambda-sam-eu-west-1-471112856731"

    override fun createGame(): String {
        return runBlocking {
            println("Creating game")
            val id = putGame(tableName)
            id
        }
    }

    override fun getContainer(gameId: String): GameContainer? {
        return runBlocking {
            println("Fetching game")
            getSpecificItem(tableName, "gameId", gameId)
        }
    }

    private suspend fun putGame(tableNameVal: String): String {
        var basicClient = DynamoDbClient.builder()
            .region(Region.EU_WEST_1)
            .build();
        var client = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(basicClient)
            .build();
        val customerTable : DynamoDbTable<DynamoDbGame> = client.table(tableNameVal, gameTableSchema);

        customerTable.

//        val itemValues = mutableMapOf<String, AttributeValue>()
        val id = RandomStringUtils.random(6, true, false)!!.uppercase()
        // Add all content to the table.
        itemValues["id"] = AttributeValue.S(id)
        itemValues["actionCount"] = AttributeValue.N("0")
        itemValues["playerCount"] = AttributeValue.N("0")
        itemValues["players"] = AttributeValue.L(listOf())

        val request =
            PutItemRequest {
                tableName = tableNameVal
                item = itemValues
            }

         { region = "eu-west-1" }.use { ddb ->
            ddb.putItem(request)
            println("Added $id to the Movie table.")
        }
        return id
    }

    suspend fun getSpecificItem(
        tableNameVal: String,
        keyName: String,
        keyVal: String
    ) : GameContainer {
        val keyToGet = mutableMapOf<String, AttributeValue>()
        keyToGet[keyName] = AttributeValue.S(keyVal)

        val request =
            GetItemRequest {
                key = keyToGet
                tableName = tableNameVal
            }

        DynamoDbClient { region = "us-east-1" }.use { ddb ->
            val returnedItem = ddb.getItem(request)
            val numbersMap = returnedItem.item
            numbersMap?.forEach { key1 ->
                println(key1.key)
                println(key1.value)
            }
        }
    }


}