import com.google.gson.Gson
import io.appwrite.Client
import io.appwrite.Query
import io.appwrite.services.Storage
import kotlinx.coroutines.delay

/*
  'req' variable has:
    'headers' - object with request headers
    'payload' - request body data as a string
    'variables' - object with function variables

  'res' variable has:
    'send(text, status)' - function to return text response. Status code defaults to 200
    'json(obj, status)' - function to return JSON response. Status code defaults to 200
  
  If an error is thrown, a response with code 500 will be returned.
*/

@Throws(Exception::class)
suspend fun main(req: RuntimeRequest, res: RuntimeResponse): RuntimeResponse {
    val QUERY_LIMIT = 100
    val HTTP_BAD_REQEST = 400

    // Check for API endpoint and key in the variables
    if (req.variables["APPWRITE_FUNCTION_ENDPOINT"] == null
        || req.variables["APPWRITE_FUNCTION_API_KEY"] == null) {

        return res.json(
            data = mapOf(
                "success" to false,
                "message" to "Environment variables are not set, Function cannot use Appwrite SDK"
            ),
            statusCode = HTTP_BAD_REQEST
        )
    }

    // Check for bucketId in the payload
    val payloadMap = Gson().fromJson<Map<String, String>>(
        req.payload.ifBlank { "{}" },
        Map::class.java)
    val bucketId = payloadMap["bucketId"] ?: ""

    if (bucketId.isEmpty()) {
        return res.json(
            data = mapOf(
                "success" to false,
                "message" to "Payload doesn't contain 'bucketId'"
            ),
            statusCode = HTTP_BAD_REQEST
        )
    }

    val client = Client()
        .setEndpoint(req.variables["APPWRITE_FUNCTION_ENDPOINT"]!!)
        .setProject(req.variables["APPWRITE_FUNCTION_PROJECT_ID"]!!)
        .setKey(req.variables["APPWRITE_FUNCTION_API_KEY"]!!)

    val storage = Storage(client)

    // Get list of files in batches and continue until files returned equals files available
    // Appwrite API will return at most 100 records at a time
    do {
        val fileList = storage.listFiles(
            bucketId = bucketId,
            queries = listOf(Query.limit(QUERY_LIMIT))
        )
        println("Fetched ${fileList.files.size} files out of total ${fileList.total}")

        // Delete the files returned in current batch
        for (file in fileList.files) {
            storage.deleteFile(bucketId, file.id)
            println("\tDeleted file: ${file.id}")
        }

        // A delay to avoid overwhelming the server
        delay(1000)

    } while (fileList.files.size < fileList.total)

    return res.json(mapOf("success" to true))
}
