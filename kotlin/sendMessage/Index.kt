
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

fun sendEmailMailgun(variables: map<string, string>, email: string, message: string, subject: string): RuntimeResponse{
    return res.json(mapOf(
    "success" to true,
    "message" to "You called sendEmailMailgun",))
}

fun sendMessageDiscordWebhook(variables: map<string, string>, message: string): RuntimeResponse{
    return res.json(mapOf(
    "success" to true,
    "message" to "You called sendMessageDiscordWebhook"))
}

fun sendSmsTwilio(variables: map<string, string>, phoneNumber: string, message: string): RuntimeResponse{
    return res.json(mapOf(
    "success" to true,
    "message" to "You called sendSmsTwilio"))

fun sendTweet(variables: map<string, string>, message: string): RuntimeResponse{
    return res.json(mapOf(
    "success" to true,
    "message" to "You called sendTweet"))
}


@Throws(Exception::class)
suspend fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse 
{   
    var result: Map<String, Any> 
    try
    {
        //Convert JSON string "payload" to a map "payloadMap"  
        val payloadMap = Gson().fromJson<Map<String, String>>(
            req.payload.ifBlank { "{}" },
            Map::class.java)       
        val payloadType = payloadMap["type"]
        val message = payloadMap["message"]

        if (payloadType == "Email")
        {
            val receiver = payloadMap["receiver"]
            val subject = payloadMap["subject"]
            result = sendEmailMailgun(req.variables, receiver, message, subject)
        }
        else if (payloadType == "SMS")
        {
            val receiver = payloadMap["receiver"]
            val result = sendSMSTwilio(req.variables, receiver, message)
        }
        else if (payloadType == "Discord")
        {
            result = sendMessageDiscord(req.variables, message)
        }
        else if (payloadType == "Twitter")
        {
            result = sendTweet(req.variables, message)
        }
        else
        {
            result = mapOf("success" to false,
                           "message" to "Invalid Type")
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
