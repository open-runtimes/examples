import java.util.Map;
import java.util.HashMap;
import java.util.*;
import io.openruntimes.java.*;
import com.google.gson.Gson;
import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Databases;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import okhttp3.Response;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

final Gson gson = new Gson();

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) throws Exception {
    // Initialize the Appwrite client and databases service
    Client client = new Client();
    var variables = req.getVariables();
    String payloadString = req.getPayload().toString();
    Map<String, Object> responseData = new HashMap<>();

    // Check if the required environment variables are set
    if (variables == null
            || !variables.containsKey("APPWRITE_FUNCTION_ENDPOINT")
            || !variables.containsKey("APPWRITE_FUNCTION_API_KEY")
            || !variables.containsKey("APPWRITE_FUNCTION_PROJECT_ID")
            || variables.get("APPWRITE_FUNCTION_ENDPOINT") == null
            || variables.get("APPWRITE_FUNCTION_API_KEY") == null
            || variables.get("APPWRITE_FUNCTION_PROJECT_ID") == null) {
        return res.json(Map.of("Environment variables are not set. Function cannot use Appwrite SDK.", false));
    } else {
        // Set the Appwrite client properties
        client
            .setEndpoint(variables.get("APPWRITE_FUNCTION_ENDPOINT"))
            .setProject(variables.get("APPWRITE_FUNCTION_PROJECT_ID"))
            .setKey(variables.get("APPWRITE_FUNCTION_API_KEY"));
    }

    try {
        // Parse the payload data into a Map
        Databases databases = new Databases(client);
        Map<String, String> payload = gson.fromJson(payloadString, Map.class);
        String databaseId = (String) payload.get("databaseId");
        String collectionId = (String) payload.get("collectionId");

        if (databaseId == null || collectionId == null) {
            return res.json(Map.of("Invalid payload.", false));
        }

        databases.deleteCollection(
            databaseId,
            collectionId,
            new Continuation<Object>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {
                    String json = "";
                    try {
                        if (o instanceof Result.Failure) {
                            Result.Failure failure = (Result.Failure) o;
                            throw failure.exception;
                        } else {
                            Response response = (Response) o;
                        }
                    } catch (Throwable th) {
                        System.out.print("ERROR: " +  th.toString());
                    }
                }
            }
        );

        return res.json(Map.of("success", true));
    } catch (AppwriteException e) {
        return res.json(Map.of("Collection not found.", false));
    }
}
