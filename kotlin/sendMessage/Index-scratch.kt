//import things here
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

// fun sendEmailMailgun(variables: mutableMap<String, String>, email: String, message: String, subject: String): RuntimeResponse
// {
//     /*** Send email using Mailgun ***/
//     if (email.equals("") || message.equals("") || subject.equals("")
//     {    
//         throw Exception("Missing email, message or subject")
//     }

//     domain = variables["MAILGUN_DOMAIN"]
//     api_key = variables["MAILGUN_API_KEY"]

//     if (domain.equals(""))
//         throw Exception("Missing Mailgun domain")
//     if (api_key.equals("")):
//         throw Exception("Missing Mailgun API key")
    
//     try
//     {

//     }
//     catch (e: Exception)
//     {

//     }
// }

@Throws(Exception::class)
suspend fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse 
{
    //Start easy with Discord, assume no exceptions
    var variables = req["VARIABLES"]
    var webhook = variables["DISCORD_WEBHOOK_URL"]
    var payload = req["payload"]
    var type = payload["type"]
    var message = payload["message"]

    //1. Open connection
    val url = URL(webhook)
    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
    //2. Make a POST request
    conn.requestMethod = "POST"
    conn.addRequestProperty("Content-Type", "application/json")
    conn.doOutput = true
    //3. Parse/Handle the response (in progress...)

    //4. Clode connection and return message
    conn.disconnect()
    return res.json(mapOf(
        "message" to "Test Part 3",
        "success" to true,
        "variabled" to req.variables,
        "payload:))" to req.payload
    ))
}