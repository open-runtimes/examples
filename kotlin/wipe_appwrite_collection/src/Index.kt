import com.google.gson.Gson
import io.appwrite.Client
import io.appwrite.services.Databases
import io.appwrite.models.DocumentList

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

    

    // Check for databaseId and collectionId in the payload
    val payloadMap = Gson().fromJson<Map<String, String>>(
        req.payload.ifBlank { "{}" },
        Map::class.java)
    val databaseId = payloadMap["databaseId"] ?: ""
    val collectionId = payloadMap["collectionId"] ?: ""
    
    if (databaseId.isEmpty()) {
        return res.json(
            data = mapOf(
                "success" to false,
                "message" to "Payload doesn't contain 'databaseId'"
            ),
            statusCode = HTTP_BAD_REQEST
        )
    }

    if (collectionId.isEmpty()) {
        return res.json(
            data = mapOf(
                "success" to false,
                "message" to "Payload doesn't contain 'collectionId'"
            ),
            statusCode = HTTP_BAD_REQEST
        )
    }

    val client = Client()
        .setEndpoint(req.variables["APPWRITE_FUNCTION_ENDPOINT"]!!)
        .setProject(req.variables["APPWRITE_FUNCTION_PROJECT_ID"]!!)
        .setKey(req.variables["APPWRITE_FUNCTION_API_KEY"]!!)
        .setSelfSigned(true)

    var done = false
    val databases = Databases(client)    

    var document_list: DocumentList? = null; 
    try {
        document_list = databases.listDocuments(
            databaseId = databaseId,
            collectionId = collectionId,
        );
    }
    catch (e: Exception) {
        return res.json(
            data = mapOf(
                "success" to false,
                "message" to "Failed to get documents list."
            ),
            statusCode = HTTP_BAD_REQEST
        )
    }

    try {
        document_list.documents.forEach{
            databases.deleteDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = it.id
            )
        }
    }
    catch (e: Exception) {
        return res.json(
            data = mapOf(
                "success" to false,
                "message" to "Failed to delete some documents."
            ),
            statusCode = HTTP_BAD_REQEST
        )
    }

    return res.json(mapOf("success" to true))
}