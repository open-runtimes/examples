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
    return mapOf(
        "success" to false, 
        "message" to message.toString()
    )
}

fun sendEmailMailgun(variables: Map<String, String>, email: String?, message: String?, subject: String?): Map<String, Any>{
    if (email.isNullOrEmpty() || message.isNullOrEmpty() || subject.isNullOrEmpty()){
        return getErrorResponseWithMessage("Missing email, message, or subject")
    }

    val domain = variables["MAILGUN_DOMAIN"]
    val apiKey = variables["MAILGUN_API_KEY"]
    
    if (domain.isNullOrEmpty()){
        return getErrorResponseWithMessage("Missing Mailgun domain")
    }
    if (apiKey.isNullOrEmpty()){
        return getErrorResponseWithMessage("Missing Mailgun API key")
    }

    val url = URL("https://api.mailgun.net/v3/$domain/messages")
    val auth = "api:$apiKey"
    val authEncoded = Base64.getEncoder().encodeToString(auth.toByteArray(StandardCharsets.UTF_8))
    val emailData = "from=<welcome@my-awesome-app.io>&to=$email&subject=$subject&text=$message"
    
    val connection = url.openConnection() as HttpURLConnection
    connection.doOutput = true
    connection.requestMethod = "POST"
    connection.addRequestProperty("Authorization", "Basic $authEncoded")
    
    val outputStream: OutputStream = connection.outputStream
    outputStream.write(emailData.toByteArray(Charsets.UTF_8))
    outputStream.flush()

    val responseCode = connection.responseCode
    val responseMessage = connection.responseMessage
    connection.disconnect()
    if (responseCode != HttpURLConnection.HTTP_OK) {
        return getErrorResponseWithMessage("$responseCode - $responseMessage")
    }
    
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
    } catch (e: JsonSyntaxException) { // if the payload is not a valid JSON or does not match the expected structure
        result = getErrorResponseWithMessage("Invalid JSON payload")
    } catch (e: IOException) { // if there is an issue with reading the payload from the request or writting the response
        result = getErrorResponseWithMessage("I/O error occurred")
    } catch (e: Exception) {
        result = getErrorResponseWithMessage("$e.message")
    }

    return res.json(result)
}