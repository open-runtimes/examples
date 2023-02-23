import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import io.openruntimes.kotlin.RuntimeRequest
import io.openruntimes.kotlin.RuntimeResponse
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


val PROVIDER_API_MAP = mapOf("bitly" to "https://api-ssl.bitly.com/v4/shorten",
                            "tinyurl" to "https://api.tinyurl.com/create")

val PROVIDER_API_URL_KEY = mapOf("bitly" to "long_url","tinyurl" to "url")

val gson = Gson()

fun getErrorResponseWithMessage(res: RuntimeResponse, message: String? = "Some error occurred"): RuntimeResponse {
    return res.json(
        data = mapOf(
            "success" to false,
            "message" to message
        ),
        statusCode = 400
    )
}

@Throws(Exception::class)
suspend fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse {

    val payloadMap = Gson().fromJson<Map<String, String>>(
        req.payload.ifBlank { "{}" },
        Map::class.java
    )

    val provider = payloadMap["provider"]?:""
    val longUrl = payloadMap["url"] ?: ""
    val providerApiKey = req.variables["PROVIDER_API_KEY"]?:""
    var providerResponse=""

    try{

        if (provider.isEmpty() || provider.trim().isEmpty()) {
            return getErrorResponseWithMessage(res, "Payload doesn't contain 'provider'")
        }
        if (longUrl.isEmpty() || longUrl.trim().isEmpty()) {
            return getErrorResponseWithMessage(res, "Payload doesn't contain 'url'")
        }

        if(!PROVIDER_API_MAP.contains(provider)){
            return getErrorResponseWithMessage(res, "Payload doesn't contain valid provider - '$provider'.")
        }

        val providerUrl = URL(PROVIDER_API_MAP.get(provider))

        val connection  = createConnection(providerUrl,providerApiKey)

        val requestBody ="{\"${PROVIDER_API_URL_KEY.get(provider)}\":\"$longUrl\"}"

        providerResponse= parseResponse(connection,requestBody,provider)
    }
    catch (e: Exception){
        return getErrorResponseWithMessage(res, e.message)
    }

    return res.json(
        data = mapOf(
            "success" to true,
            "url" to providerResponse
        ),
        statusCode = 200
    )
}

fun createConnection(providerUrl:URL,providerApiKey:String): HttpURLConnection {
    val connection: HttpURLConnection = providerUrl.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.addRequestProperty("Content-Type", "application/json")
    connection.addRequestProperty("Authorization", "Bearer $providerApiKey")
    connection.setRequestProperty("accept", "application/json")
    connection.doOutput = true
    return connection
}

fun parseResponse(connection:HttpURLConnection,requestBody:String,provider:String): String {
    val response = StringBuilder()
    val os: OutputStream = connection.getOutputStream()
    val input: ByteArray = requestBody.toByteArray(Charsets.UTF_8)
    os.write(input, 0, input.size)

    val br = BufferedReader(InputStreamReader(connection.getInputStream(), Charsets.UTF_8))

    var responseLine: String? = null

    while (br.readLine().also { responseLine = it } != null) {
        response.append(responseLine!!.trim { it <= ' ' })
    }

    br.close()
    connection.disconnect()

    val jsonObject = JsonParser().parse(response.toString()).getAsJsonObject()

    return when {
        provider == "bitly" -> jsonObject.get("link").getAsString()
        else -> jsonObject.getAsJsonObject("data").get("tiny_url").getAsString()
    }

}

