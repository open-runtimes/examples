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
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.io.OutputStreamWriter
//Tweeter deps below
import com.chromasgaming.ktweet.constants.VERSION
import com.chromasgaming.ktweet.models.*
// import com.chromasgaming.ktweet.models.Tweet
import com.chromasgaming.ktweet.oauth.SignatureBuilder
import com.chromasgaming.ktweet.oauth.buildSignature
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

fun getErrorResponseWithMessage(message: String? = "Some error occurred"): Map<String, Any> {
    return mapOf(
            "success" to false,
            "message" to message.toString()
        )
}

fun sendEmailMailgun(variables: Map<String, String>, email: String?, message: String?, subject: String?): Map<String, Any>{
    return mapOf("success" to true,
                 "message" to "You called sendEmailMailgun")
}

fun sendMessageDiscordWebhook(variables: Map<String, String>, message: String?): Map<String, Any>{
    val webhook = variables["DISCORD_WEBHOOK_URL"]?:""

    try {
        if (webhook.isEmpty() || webhook.trim().isEmpty()) {
            return getErrorResponseWithMessage("Payload doesn't contain a Discord Webhook URL")
        }

        // println(webhook)   //************ */
        val url = URL(webhook)

        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        val body = "{\"content\":\"$message\"}"
        conn.addRequestProperty("Content-Type", "application/json")
        conn.requestMethod = "POST"
        conn.doOutput = true

        val os: OutputStream = conn.outputStream
        val input: ByteArray = body.toByteArray(Charsets.UTF_8)
        // println(input)   //********** */
        os.write(input)

        val responseCode: Int = conn.responseCode // To Check for 200
        // println(responseCode)  //************ */
        os.close()
        conn.disconnect()
        if (responseCode / 100 == 2) {    //HTTP code of 2xx means success (most of the time)
            return mapOf("success" to true,
            "message" to "You called sendMessageDiscordWebhook")
        }
        else {
            return getErrorResponseWithMessage(conn.getResponseMessage())
            // return mapOf("success" to false,
            //             "message" to conn.getResponseMessage())    
        } 

    } catch (e: IllegalArgumentException) { // if variable receiver is set to "invalid"
        // println("First was executed")
        return getErrorResponseWithMessage(e.message)
        // return mapOf("success" to false, "message" to "Error: ${e.message}")
    } catch (e: IOException) { // if network-related issues such as failure to establish a connection or send the HTTP request to the Twilio APi this will catch it
        // println("Second was executed")
        return getErrorResponseWithMessage(e.message)
        // return mapOf("success" to false, "message" to "Error: ${e.message}")
    }
}

fun sendSmsTwilio(variables: Map<String, String>, receiver: String?, message: String?): Map<String, Any>{

    val accountID = variables.get("TWILIO_ACCOUNT_SID") // Acount SID from Twilio
    val authToken  = variables.get("TWILIO_AUTH_TOKEN") // Auth Token from Twilio
    val sender = variables.get("TWILIO_SENDER") // Sender Phone Number from Twilio | Mandatory format: +# ### ### #### (all together)

    
    if (accountID.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Account ID is not set")
    }

    if (authToken.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Auth token is not set")
    }

    if (sender.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Sender is not set")
    }

    if (receiver.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Receiver is not set")
    }
    if (message.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Message is not set")
    }


    try {
        val urlString= "https://api.twilio.com/2010-04-01/Accounts/$accountID/Messages.json"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        val authString = "$accountID:$authToken "
        val authEncoded = Base64.getEncoder().encodeToString(authString.toByteArray(StandardCharsets.UTF_8))
        connection.setRequestProperty("Authorization", "Basic $authEncoded")    

        val postData = "To=$receiver&From=$sender&Body=$message"
            
        val outputStreamWriter = OutputStreamWriter(connection.outputStream)
        outputStreamWriter.write(postData)
        outputStreamWriter.flush()
    
    
        val responseCode = connection.responseCode
        val responseMessage = connection.responseMessage
    
        connection.disconnect()

        if (responseCode != HttpURLConnection.HTTP_CREATED) { // HttpURLConnection.HTTP_CREATED = 201 Created
            return mapOf("success" to false, "message" to "Error: #$responseCode  - > $responseMessage")
        } 

        return mapOf("success" to true, "message" to "Message sent!")

    } catch (e: IllegalArgumentException) { // if variable receiver is set to "invalid"
        return mapOf("success" to false, "message" to "Error: ${e.message}")
    } catch (e: IOException) { // if network-related issues such as failure to establish a connection or send the HTTP request to the Twilio APi this will catch it
        return mapOf("success" to false, "message" to "Error: ${e.message}")
    }
}

fun sendTweet(variables: Map<String, String>, message: String?): Map<String, Any>{
    val apiKey = variables["TWITTER_API_KEY"]
    val apiSecret = variables["TWITTER_API_KEY_SECRET"]
    val accessToken = variables["TWITTER_ACCESS_TOKEN"]
    val accessTokenSecret = variables["TWITTER_ACCESS_TOKEN_SECRET"]

    var post: ManageTweets
    manageTweets = ManageTweetsAPI()

    signatureBuilder = SignatureBuilder.Builder()
            .oauthConsumerKey(apiKey)
            .oauthConsumerSecret(apiSecret)
            .accessToken(accessToken)
            .accessTokenSecret(accessTokenSecret)
            .build()

    authorizationHeaderString = buildSignature(
            "POST",
            signatureBuilder,
            "$VERSION/tweets",
            emptyMap()
        )

    val tweet = Tweet(message)
    post = manageTweets.create(tweet, authorizationHeaderString)
    /*How can I use <post> to check if the request was sucessful??? 
      Seems like it's a JSON object; represents the body response*/
    return mapOf("success" to true, "message" to post.text)
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