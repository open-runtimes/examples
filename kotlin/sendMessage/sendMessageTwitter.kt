import java.net.URLEncoder


fun sendTweet(variables: Map<String, String>, message: String?): Map<String, Any> {
    val apiKey = variables["TWITTER_API_KEY"]
    val apiSecret = variables["TWITTER_API_KEY_SECRET"]
    val accessToken = variables["TWITTER_ACCESS_TOKEN"]
    val accessTokenSecret = variables["TWITTER_ACCESS_TOKEN_SECRET"]

    if (apiKey.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Api Key is not set")
    }

    if (apiSecret.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Api Secret is not set")
    }

    if (accessToken.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Access Token is not set")
    }

    if (accessTokenSecret.isNullOrEmpty()) {
        return mapOf("success" to false, "message" to "Access Token Secret is not set")
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