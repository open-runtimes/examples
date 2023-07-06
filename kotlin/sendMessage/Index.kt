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
fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse 
{   
    var result: Map<String, Any> = mapOf("" to "") 
    try
    {
        //Convert JSON String "payload" to a Map "payloadMap"  
        val payloadMap = Gson().fromJson<Map<String, String>>(
            req.payload.ifBlank { "{}" },
            Map::class.java)       
        val payloadType = payloadMap["type"]
        val message = payloadMap["message"]

        when (payloadType) 
        {
            "Email" -> {
                val receiver = payloadMap["receiver"]
                val subject = payloadMap["subject"]
                result = sendEmailMailgun(req.variables, receiver, message, subject)
            }
            "SMS" -> {
                val receiver = payloadMap["receiver"]
                result = sendSmsTwilio(req.variables, receiver, message)
            }
            "Discord" -> {
                result = sendMessageDiscordWebhook(req.variables, message)
            }
            "Twitter" -> {
                result = sendTweet(req.variables, message)
            }
            else -> {
                result = mapOf("success" to false,
                            "message" to "Invalid Type")
            }
        }
    }
    catch (e: Exception)
    {
        return res.json(mapOf(
        "success" to false,
        "message" to e.message,
        ))
    }
    return res.json(result)
}