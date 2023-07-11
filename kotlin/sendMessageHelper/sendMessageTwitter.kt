import java.net.URLEncoder
import com.chromasgaming.ktweet.api //for creating a Tweet post
import com.chromasgaming.ktweet.models //for creating a Tweet object 

tweet: Tweet  = Tweet(message)
authorizationHeaderString: String = 

/* Authorization reference:
    header 'Authorization: OAuth oauth_consumer_key="CONSUMER_API_KEY", 
            oauth_nonce="OAUTH_NONCE", 
            oauth_signature="OAUTH_SIGNATURE", 
            oauth_signature_method="HMAC-SHA1", 
            oauth_timestamp="OAUTH_TIMESTAMP", 
            oauth_token="ACCESS_TOKEN", 
            oauth_version="1.0"'    */

fun sendTweet(variables: Map<String, String>, message: String?): Map<String, Any> {
    val apiKey = variables["TWITTER_API_KEY"]
    val apiSecret = variables["TWITTER_API_KEY_SECRET"]
    val accessToken = variables["TWITTER_ACCESS_TOKEN"]
    val accessTokenSecret = variables["TWITTER_ACCESS_TOKEN_SECRET"]

    if (apiKey.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Api Key is not set")
    }

    if (apiSecret.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Api Secret is not set")
    }

    if (accessToken.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Access Token is not set")
    }

    if (accessTokenSecret.isNullOrEmpty()) {
        return getErrorResponseWithMessage("Access Token Secret is not set")
    }

    try {
        val url = URL("https://api.twitter.com/1.1/statuses/update.json?")
        val connection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Bearer ${URLEncoder.encode(accessToken, "UTF-8")}")
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        connection.setRequestProperty("Accept", "application/json")

        val requestBody = "status=${URLEncoder.encode(message, "UTF-8")}"

        val outputStream = connection.outputStream
        outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
        outputStream.close()

        val responseCode = connection.responseCode
        val responseMessage = connection.responseMessage

        if (responseCode != HttpURLConnection.HTTP_OK) {
            return mapOf("success" to false, "message" to "Error: #$responseCode - > $responseMessage")
        }

        connection.disconnect()

        return mapOf("success" to true, "message" to "Tweet sent!")
    } catch (e: Exception) {
        return mapOf("success" to false, "message" to "An unexpected error occurred: ${e.message}")
    }
}