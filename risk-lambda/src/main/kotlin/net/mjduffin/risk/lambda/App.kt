package net.mjduffin.risk.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.mjduffin.risk.lib.Draft
import net.mjduffin.risk.lib.RiskController
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors

/**
 * Handler for requests to Lambda function.
 */
class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private val log : Logger = LoggerFactory.getLogger(App::class.java)
    // map of game ID to game container
    val mapper = jacksonObjectMapper()
    private val restController: RiskController = RiskController(DynamoDbGameContainerService());

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context?): APIGatewayProxyResponseEvent {
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["X-Custom-Header"] = "application/json"

        val app: HttpHandler = routes(
            "/games/{gameId}/turn/draft" bind Method.POST to { req: Request ->
                val gameId: String = req.path("gameId")!!
                val draft = mapper.readValue<Draft>(req.body.stream)
                val gameResponse = restController.draft(gameId, draft)
                val writeValue = mapper.writeValueAsString(gameResponse)
                Response(OK).body(writeValue)
            },
//            "/ping" bind Method.GET to { _: Request -> Response(OK).body("pong!") },
//            "/greet/{name}" bind Method.GET to { req: Request ->
//                val name: String? = req.path("name")
//                Response(OK).body("hello ${name ?: "anon!"}")
//            }
        )
        // check http method and convert to kotlin version
        input.httpMethod

        app.invoke(Request(input.httpMethod, ))

        val response: APIGatewayProxyResponseEvent = APIGatewayProxyResponseEvent()
            .withHeaders(headers)
        try {
            val pageContents = this.getPageContents("https://checkip.amazonaws.com")
            val output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents)

            return response
                .withStatusCode(200)
                .withBody(output)
        } catch (e: IOException) {
            return response
                .withBody("{}")
                .withStatusCode(500)
        }
    }

    @Throws(IOException::class)
    private fun getPageContents(address: String): String {
        val url = URL(address)
        BufferedReader(InputStreamReader(url.openStream())).use { br ->
            return br.lines().collect(
                Collectors.joining(
                    System.lineSeparator()
                )
            )
        }
    }
}
