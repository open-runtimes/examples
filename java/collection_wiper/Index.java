import io.appwrite.Client;
import io.appwrite.services.Databases;
import io.appwrite.services.Exceptions.AppwriteException;
import io.appwrite.services.Models.DocumentList;
import io.appwrite.services.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Index {
    public static void main(String[] args) {
        String endpoint = System.getenv("APPWRITE_FUNCTION_ENDPOINT");
        String apiKey = System.getenv("APPWRITE_FUNCTION_API_KEY");
        String projectId = System.getenv("APPWRITE_FUNCTION_PROJECT_ID");

        if (endpoint == null || apiKey == null || projectId == null) {
            System.out.println("{\"success\": false, \"message\": \"Variables missing.\"}");
            return;
        }

        Client client = new Client();
        client
            .setEndpoint(endpoint)
            .setProject(projectId)
            .setKey(apiKey);

        Databases databases = new Databases(client);

        try {
            JsonObject payload = JsonParser.parseString("{}").getAsJsonObject();
            if (!payload.has("databaseId") || !payload.has("collectionId")) {
                System.out.println("{\"success\": false, \"message\": \"Invalid payload.\"}");
                return;
            }

            int sum = 0;
            boolean done = false;

            while (!done) {
                Response<DocumentList> response = databases.listDocuments(
                    payload.get("databaseId").getAsString(),
                    payload.get("collectionId").getAsString()
                ).execute();

                if (response.getStatusCode() != 200) {
                    throw new AppwriteException("Failed to list documents");
                }

                DocumentList result = response.getBody();

                if (result == null || result.getDocuments() == null) {
                    done = true;
                    break;
                }

                for (JsonObject document : result.getDocuments()) {
                    Response<Void> deleteResponse = databases.deleteDocument(
                        payload.get("databaseId").getAsString(),
                        payload.get("collectionId").getAsString(),
                        document.get("$id").getAsString()
                    ).execute();

                    if (deleteResponse.getStatusCode() == 200) {
                        sum++;
                    } else {
                        throw new AppwriteException("Failed to delete document");
                    }
                }

                if (result.getDocuments().size() == 0) {
                    done = true;
                }
            }

            System.out.println("{\"success\": true, \"sum\": " + sum + "}");
        } catch (AppwriteException e) {
            System.out.println("{\"success\": false, \"message\": \"Unexpected error: " + e.getMessage() + "\"}");
        }
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

    if(req.getPayload().isEmpty()||req.getPayload().trim().equals("{}")){
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