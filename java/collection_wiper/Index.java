import io.appwrite.Client;
import io.appwrite.services.Databases;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.DocumentList;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import io.openruntimes.java.*;
import java.util.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) {
    // Validate that values present in the request are not empty (payload, variables)
    RuntimeResponse errorResponse=checkEmptyPayloadAndVariables(req,res);
    if(errorResponse!=null){
        return errorResponse;
    }
    String endpoint = req.getVariables().get("APPWRITE_FUNCTION_ENDPOINT");
    String apiKey = req.getVariables().get("APPWRITE_FUNCTION_API_KEY");
    String projectId = req.getVariables().get("APPWRITE_FUNCTION_PROJECT_ID");

    Map<String, Object> responseData = new HashMap<>();
    if (endpoint == null || apiKey == null || projectId == null) {
        responseData.put("success",false);
        responseData.put("message","Variables missing.");
        return res.json(responseData);
    }

    Client client = new Client();
    client
        .setEndpoint(endpoint)
        .setProject(projectId)
        .setKey(apiKey);

    Databases databases = new Databases(client);
    Gson gson=new Gson();
    try {
        String payloadString = req.getPayload().toString();

        Map<String, String> payload = gson.fromJson(payloadString, Map.class);
        // Get file url from payload

        String databaseId = payload.get("databaseId");
        String collectionId = payload.get("collectionId");
        if ( databaseId == null || collectionId == null) {
            responseData.put("success",false);
            responseData.put("message","Invalid payload.");
            return res.json(responseData);
        }
        databases.deleteCollection(databaseId, collectionId,
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
                        System.out.println("ERROR: " +  th.toString());
                    }
                }
            }
        );

        responseData.put("success",true);
        return res.json(responseData);
    } catch (AppwriteException e) {
        responseData.put("success",true);
        responseData.put("message","Unexpected error: " + e.getMessage());
        return res.json(responseData);
    }
}

/**
 * This function will validate that the payload and variables are non-empty in the request
 *
 * @param req is the received POST request
 * @return null is nothing is empty, otherwise an error response
 */
private RuntimeResponse checkEmptyPayloadAndVariables(RuntimeRequest req,RuntimeResponse res){

        Map<String, Object> responseData=new HashMap<>();

        if(req.getPayload()==null||req.getPayload().trim().isEmpty()||req.getPayload().trim().equals("{}")){
            responseData.put("success",false);
            responseData.put("message","Payload is empty, expected a payload with provider and URL");
            return res.json(responseData);

        }
        if(req.getVariables()==null){
            responseData.put("success",false);
            responseData.put("message","Empty function variables found. You need to pass an API key for the provider");
            return res.json(responseData);
        }

        return null;
}