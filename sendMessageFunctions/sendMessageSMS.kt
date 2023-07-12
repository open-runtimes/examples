import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.Map


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
