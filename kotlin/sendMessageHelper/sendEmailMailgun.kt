import java.util.Base64
import java.nio.charset.StandardCharsets

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
