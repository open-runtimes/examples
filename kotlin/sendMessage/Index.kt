import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.openruntimes.kotlin.RuntimeRequest
import io.openruntimes.kotlin.RuntimeResponse
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64
import java.nio.charset.StandardCharsets

fun getErrorResponseWithMessage(message: String? = "Some error occurred"): Map<String, Any> {
    return mapOf{
        "success" to false, 
        "message" to message.toString()
    }
}

fun sendEmailMailgun(variables: Map<String, String>, email: String?, message: String?, subject: String?): Map<String, Any>{
    return mapOf("success" to true,
                 "message" to "You called sendEmailMailgun")
}


fun sendMessageDiscordWebhook(variables: Map<String, String>, message: String?): Map<String, Any>{
        return mapOf("success" to true,
                     "message" to "You called sendMessageDiscordWebhook")
}

fun sendSmsTwilio(variables: Map<String, String>, receiver: String?, message: String?): Map<String, Any>{
    return mapOf("success" to true,
                 "message" to "You called sendSmsTwilio")
}

fun sendTweet(variables: Map<String, String>, message: String?): Map<String, Any>{
    return mapOf("success" to true,
                 "message" to "You called sendTweet")
}

@Throws(Exception::class)
fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse {
    var result: Map<String, Any> = emptyMap()
    try {
        val payloadMap = Gson().fromJson<Map<String, String>>(
            req.payload.ifBlank { "{}" },
            Map::class.java
        )
        val payloadType = payloadMap["type"]
        val message = payloadMap["message"]

        result = when (payloadType) {
            "Email" -> {
                val receiver = payloadMap["receiver"]
                val subject = payloadMap["subject"]
                sendEmailMailgun(req.variables, receiver, message, subject)
            }
            "SMS" -> {
                val receiver = payloadMap["receiver"]
                sendSmsTwilio(req.variables, receiver, message)
            }
            "Discord" -> {
                sendMessageDiscordWebhook(req.variables, message)
            }
            "Twitter" -> {
                sendTweet(req.variables, message)
            }
            else -> {
                mapOf(
                    "success" to false,
                    "message" to "Invalid Type"
                )
            }
        }
    } catch (e: JsonSyntaxException) { // if payload is not a valid JSON or does not match the expected structure it will catch that
        return res.json(
            mapOf(
                "success" to false,
                "message" to "Invalid JSON payload"
            )
        )
    } catch (e: IOException) { // if there is an issue with reading the payload from the request or writting the response it catches that
        return res.json(
            mapOf(
                "success" to false,
                "message" to "I/O error occurred"
            )
        )
    } catch (e: Exception) { // if any other unhandled exception occurrs this catches that
        return res.json(
            mapOf(
                "success" to false,
                "message" to e.message
            )
        )
    }

    return res.json(result)
}