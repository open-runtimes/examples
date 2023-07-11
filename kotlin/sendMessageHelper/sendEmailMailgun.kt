import java.util.Base64

fun sendEmailMailgun(variables: Map<String, String>, email: String?, message: String?, subject: String?): Map<String, Any>{
    try {
        if (email.isNullOrEmpty() || message.isNullOrEmpty() || subject.isNullOrEmpty()){
            throw Exception("Missing email, message, or subject")
        }
    } catch (e: Exception) {
        println(e.message)
        return mapOf("success" to false,
                     "message" to "sendEmailMailgun failed")
    }

    val domain = variables["MAILGUN_DOMAIN"]
    val apiKey = variables["MAILGUN_API_KEY"]

    try {
        if (domain.isNullOrEmpty()){
            throw Exception("Missing Mailgun domain")
        }
    } catch (e: Exception) {
        println(e.message)
        return mapOf("success" to false,
                     "message" to "sendEmailMailgun failed")
    }

    try {
        if (apiKey.isNullOrEmpty()){
            throw Exception("Missing Mailgun API key")
        }
    } catch (e: Exception) {
        println(e.message)
        return mapOf("success" to false,
                     "message" to "sendEmailMailgun failed")
    }

    try {
        // URL for the Mailgun API endpoint
        val url = URL({"https://api.mailgun.net/v3/$domain/messages"})

        // authentication
        val auth = "api:$apiKey"

        // initializing data to send
        val email_data =  "{\"from\":\"$domain\", \"to\":\"$email\", \"subject\":\"$subject\", \"text\":\"$message\"}"

        // opening the HTTP connection    
        val connection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.requestMethod = "POST"
        // setting request headers + authenticating the HTTP connection
        connection.addRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.toByteArray()))
        connection.addRequestProperty("Content-Type", "application/json")
        
        // make POST request
        val outputStream: OutputStream = connection.outputStream
        outputStream.write(email_data.toByteArray(Charsets.UTF_8))
        outputStream.flush()

        // check status of response
        val responseCode = connection.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("$responseCode")
        }
        
        // housekeeping ; closing the open connection
        outputSream.close()
        connection.disconnect()
    } catch (e: Exception) {
        println(e.message)
        return mapOf("success" to false,
                     "message" to "sendEmailMailgun failed")
    }
    
    return mapOf("success" to true,
                 "message" to "You called sendEmailMailgun")
}