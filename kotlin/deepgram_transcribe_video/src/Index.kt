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


val HTTP_BAD_REQEST = 400
val DEEPGRAM_TRANSCRIBE_VIDEO_API_END_POINT = "https://api.deepgram.com/v1/listen?model=video"
val gson = Gson()

fun getErrorResponseWithMessage(res: RuntimeResponse, message: String? = "Some error occurred"): RuntimeResponse {
    return res.json(
        data = mapOf(
            "success" to false,
            "message" to message
        ),
        statusCode = HTTP_BAD_REQEST
    )
}

@Throws(Exception::class)
suspend fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse {

    val deepgramApiKey = req.variables["DEEPGRAM_API_KEY"]
    if (deepgramApiKey == null || deepgramApiKey.trim().isEmpty()) {
        return getErrorResponseWithMessage(res, "Deepgram API key must be set to use this function.")
    }

    val payloadMap = Gson().fromJson<Map<String, String>>(
        req.payload.ifBlank { "{}" },
        Map::class.java
    )
    val fileUrl = payloadMap["fileUrl"] ?: ""

    if (fileUrl.isEmpty() || fileUrl.trim().isEmpty()) {
        return getErrorResponseWithMessage(res, "Payload doesn't contain 'fileUrl'")
    }

    val url = URL(DEEPGRAM_TRANSCRIBE_VIDEO_API_END_POINT)
    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

    conn.requestMethod = "POST"
    conn.addRequestProperty("Content-Type", "application/json")
    conn.addRequestProperty("Authorization", "Token $deepgramApiKey")

    conn.doOutput = true

    //prepare request body
    val requestBody ="{\"url\":\""+fileUrl+"\"}"

    val response = StringBuilder()

    try {
        val os: OutputStream = conn.getOutputStream()
        val input: ByteArray = requestBody.toByteArray(Charsets.UTF_8)
        os.write(input, 0, input.size)

        val br = BufferedReader(InputStreamReader(conn.getInputStream(), "utf-8"))
        var responseLine: String? = null
        while (br.readLine().also { responseLine = it } != null) {
            response.append(responseLine!!.trim { it <= ' ' })
        }

        br.close()
        conn.disconnect()
    }
    catch (e: Exception){
        return getErrorResponseWithMessage(res, e.message)
    }


    val deepgramResponse = response.toString()
    return res.json(
        data = mapOf(
            "success" to true,
            "deepgramData" to deepgramResponse
        ),
        statusCode = 200
    )
}
