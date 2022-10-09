import com.google.gson.Gson
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

val client = OkHttpClient()
val gson = Gson()

val HTTP_METHODS =
    listOf(
        "GET",
        "POST",
        "PUT",
        "DELETE",
        "PATCH",
    )

data class Payload(
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val body: String?,
)

fun validatePayload(payload: Payload): Boolean {
    if (payload.method !in HTTP_METHODS) {
        return false
    }

    if (payload.url.isBlank()) {
        return false
    }

    return true
}

@Throws(Exception::class)
fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse {
    try {
        val json: Map<String, Any> =
            gson.fromJson<Map<String, Any>>(req.payload, MutableMap::class.java)
                ?: return res.json(mapOf("success" to false, "message" to "Invalid json data"))
        val headers: Map<String, String> =
            gson.fromJson<Map<String, String>>(json["headers"].toString(), MutableMap::class.java)
                ?: mapOf()
        val payload =
            Payload(
                method = json["method"].toString(),
                url = json["url"].toString(),
                headers = headers,
                body = if (json["method"].toString() != "GET") json["body"].toString() else null,
            )
        if (!validatePayload(payload)) {
            return res.json(mapOf("success" to false, "message" to "Invalid payload: $payload"))
        }

        val request = Request.Builder()
            .url(payload.url)
            .method(payload.method, payload.body?.toRequestBody())
            .headers(payload.headers.toHeaders())
            .build()

        try {
            val response = client.newCall(request).execute()
            return res.json(
                mapOf(
                    "success" to true,
                    "response" to (response.body?.string() ?: ""),
                )
            )
        } catch (e: Exception) {
            return res.json(mapOf("success" to false, "message" to e.message))
        }
    } catch (e: Exception) {
        return res.json(
            mapOf("success" to false, "message" to "JSON payload parsing error: ${e.message}")
        )
    }
}
