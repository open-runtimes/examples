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