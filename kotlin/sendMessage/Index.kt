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
import java.io.OutputStreamWriter
import com.chromasgaming.ktweet.models.ManageTweets
import com.chromasgaming.ktweet.models.Tweet
import com.chromasgaming.ktweet.oauth.SignatureBuilder
import com.chromasgaming.ktweet.oauth.buildSignature
import com.chromasgaming.ktweet.config.ClientConfig

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

fun sendMessageDiscordWebhook(variables: Map<String, String>, message: String?): Map<String, Any> {
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
        } 

    } catch (e: IllegalArgumentException) { // if variable receiver is set to "invalid"
        return getErrorResponseWithMessage(e.message)
    } catch (e: IOException) { // if network-related issues such as failure to establish a connection or send the HTTP request to the Twilio APi this will catch it
        return getErrorResponseWithMessage(e.message)
    }
}

fun sendSmsTwilio(variables: Map<String, String>, receiver: String?, message: String?): Map<String, Any>{

    val accountID = variables.get("TWILIO_ACCOUNT_SID") // Acount SID from Twilio
    val authToken  = variables.get("TWILIO_AUTH_TOKEN") // Auth Token from Twilio
    val sender = variables.get("TWILIO_SENDER") // Sender Phone Number from Twilio | Mandatory format: +# ### ### #### (all together)

    
    if (accountID.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Account ID is not set")
    }

    if (authToken.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Auth token is not set")
    }

    if (sender.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Sender is not set")
    }

    if (receiver.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Receiver is not set")
    }
    if (message.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Message is not set")
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
        return getErrorResponseWithMessage("Error: ${e.message}")
    } catch (e: IOException) { // if network-related issues such as failure to establish a connection or send the HTTP request to the Twilio APi this will catch it
        return getErrorResponseWithMessage("Error: ${e.message}")
    }
}

fun sendTweet(variables: Map<String, String>, message: String?): Map<String, Any> {
    val apiKey = variables["TWITTER_API_KEY"]?:""
    val apiSecret = variables["TWITTER_API_KEY_SECRET"]?:""
    val accessToken = variables["TWITTER_ACCESS_TOKEN"]?:""
    val accessTokenSecret = variables["TWITTER_ACCESS_TOKEN_SECRET"]?:""

    if (apiKey.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Payload doesn't contain an API key (i.e. consumer key)")
    }

    if (apiSecret.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Payload doesn't contain an API key Secret (i.e. consumer secret)")
    }

    if (accessToken.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Payload doesn't contain an access token")
    }

    if (accessTokenSecret.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Payload doesn't contain an an access token secret")
    }

    val signatureBuilder: SignatureBuilder = SignatureBuilder.Builder()
            .oauthConsumerKey(apiKey)
            .oauthConsumerSecret(apiSecret)
            .accessToken(accessToken)
            .accessTokenSecret(accessTokenSecret)
            .build()

    val authorizationHeaderString: String = buildSignature(
            "POST",
            signatureBuilder,
            "2/tweets",
            emptyMap()
        )
    // println(authorizationHeaderString)

    val urlString = "https://api.twitter.com/2/tweets"
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.doOutput = true

    connection.setRequestProperty("Authorization", "$authorizationHeaderString")
    connection.setRequestProperty("Content-Type", "application/json")    

    val postData = "{\"text\":\"$message\"}"
    // println(postData)

    val outputStreamWriter = OutputStreamWriter(connection.outputStream)
    outputStreamWriter.write(postData)
    outputStreamWriter.flush()
    
    val responseCode = connection.responseCode
    val responseMessage = connection.responseMessage
    
    connection.disconnect()

    if (responseCode != HttpURLConnection.HTTP_CREATED) { // HttpURLConnection.HTTP_CREATED = 201 Created
        return getErrorResponseWithMessage("Error: #$responseCode  - > $responseMessage")
    } 
    return mapOf("success" to true, "message" to "You called sendTweet")
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