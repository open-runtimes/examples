import java.util.Base64
import java.nio.charset.StandardCharsets

fun sendEmailMailgun(variables: Map<String, String>, email: String?, message: String?, subject: String?): Map<String, Any>{
    // validating email, message, and subject arguments
    try {
        if (email.isNullOrEmpty() || message.isNullOrEmpty() || subject.isNullOrEmpty()){
            throw Exception("Missing email, message, or subject")
        }
    } catch (e: Exception) {
        println(e.message)
        return mapOf("success" to false,
                        "message" to "sendEmailMailgun failed")
    }

    // initializing variables needed for HTTP request
    val domain = variables["MAILGUN_DOMAIN"]
    val apiKey = variables["MAILGUN_API_KEY"]
    
    // validating Mailgun domain
    try {
        if (domain.isNullOrEmpty()){
            throw Exception("Missing Mailgun domain")
        }
    } catch (e: Exception) {
        println(e.message)
        return mapOf("success" to false,
                        "message" to "sendEmailMailgun failed")
    }
    
    // validating Mailgun API key
    try {
        if (apiKey.isNullOrEmpty()){
            throw Exception("Missing Mailgun API key")
        }
    } catch (e: Exception) {
        println(e.message)
        return mapOf("success" to false,
                        "message" to "sendEmailMailgun failed")
    }

    // making the HTTP POST request
    try {
        // URL for the Mailgun API endpoint
        val url = URL("https://api.mailgun.net/v3/$domain/messages")

        // authentication
        val auth = "api:$apiKey"
        val authEncoded = Base64.getEncoder().encodeToString(auth.toByteArray(StandardCharsets.UTF_8))

        // initializing data to send
        //val emailData =  "{\"from\":\"$domain\", \"to\":\"$email\", \"subject\":\"$subject\", \"text\":\"$message\"}"
        val emailData = "from=<welcome@my-awesome-app.io>&to=$email&subject=$subject&text=$message"
        
        // opening the HTTP connection
        val connection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.requestMethod = "POST"

        // setting request headers + authenticating the HTTP connection
        connection.addRequestProperty("Authorization", "Basic $authEncoded")
        
        // making POST request
        // val outputStreamWriter = OutputStreamWriter(connection.outputStream)
        // outputStreamWriter.write(emailData)
        // outputStreamWriter.flush()
        val outputStream: OutputStream = connection.outputStream
        outputStream.write(emailData.toByteArray(Charsets.UTF_8))
        outputStream.flush()

        // checking status of response
        val responseCode = connection.responseCode
        val responseMessage = connection.responseMessage
        println(responseMessage)
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("$responseCode")
        }

        // housekeeping ; closing the open connection
        //outputStreamWriter.close()
        connection.disconnect()

    } catch (e: Exception) {
        println(e.message)
        return mapOf("success" to false,
                        "message" to "sendEmailMailgun failed")
    }
    return mapOf("success" to true,
                    "message" to "You called sendEmailMailgun")
}
