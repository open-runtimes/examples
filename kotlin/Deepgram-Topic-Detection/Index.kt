import com.google.gson.Gson
import io.openruntimes.kotlin.RuntimeRequest
import io.openruntimes.kotlin.RuntimeResponse

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

val API_ENDPOINT = "https://api.deepgram.com/v1/listen?detect_topics=true&punctuate=true"

@Throws(Exception::class)
suspend fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse {
    try {
        val deepgramApiKey = req.variables["DEEPGRAM_API_KEY"]
        if (deepgramApiKey == null || deepgramApiKey.trim().isEmpty()) {
            throw Exception("Deepgram API key is missing.")
        }

        val payloadMap = Gson().fromJson<Map<String, String>>(
            req.payload.ifBlank { "{}" },
            Map::class.java
        )
        val fileUrl = payloadMap["fileUrl"] ?: ""

        if (fileUrl.isEmpty() || fileUrl.trim().isEmpty()) {
            throw Exception("'fileUrl is missing'")
        }

        val url = URL(API_ENDPOINT)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

        conn.requestMethod = "POST"
        conn.addRequestProperty("Content-Type", "application/json")
        conn.addRequestProperty("Authorization", "Token $deepgramApiKey")

        conn.doOutput = true

        val requestBody = "{\"url\":\"$fileUrl\"}"

        val response = StringBuilder()

        val os: OutputStream = conn.getOutputStream()
        val input: ByteArray = requestBody.toByteArray(Charsets.UTF_8)
        os.write(input, 0, input.size)

        val br = BufferedReader(InputStreamReader(conn.getInputStream(), "utf-8"))
        var responseLine: String?
        while (br.readLine().also { responseLine = it } != null) {
            response.append(responseLine!!.trim { it <= ' ' })
        }

        br.close()
        conn.disconnect()

        return mapOf(
                "success" to true,
                "deepgramData" to response.toString()
        )
    } catch (e: Exception) {
        return mapOf(
                "success" to false,
                "message" to e.message
            )
    }
}