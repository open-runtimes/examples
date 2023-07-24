import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import io.appwrite.Client;
import io.appwrite.services.Databases;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.DocumentList;

final Gson gson = new Gson();

public RuntimeResponse main(RuntimeRequest req, RuntimeResponse res) throws Exception {
        var client = new Client();
        var database = new Databases(client);

        var variables = req.getVariables();

        System.out.println("In the jungle ******");

        if (variables == null
            || !variables.containsKey("APPWRITE_FUNCTION_ENDPOINT")
            || !variables.containsKey("APPWRITE_FUNCTION_API_KEY")
            || !variables.containsKey("APPWRITE_FUNCTION_PROJECT_ID")
            || variables.get("APPWRITE_FUNCTION_ENDPOINT") == null
            || variables.get("APPWRITE_FUNCTION_API_KEY") == null
            || variables.get("APPWRITE_FUNCTION_PROJECT_ID") == null) {
            return res.json(Map.of(
                    "success", false,
                    "message", "Variables missing."
            ));
        } else {
            client
                .setEndpoint(variables.get("APPWRITE_FUNCTION_ENDPOINT"))
                .setProject(variables.get("APPWRITE_FUNCTION_PROJECT_ID"))
                .setKey(variables.get("APPWRITE_FUNCTION_API_KEY"));
        }

        try {
            JsonObject payload = gson.fromJson(req.getPayload(), JsonObject.class);
            if (!payload.has("databaseId") || !payload.has("collectionId")) {
            return res.json(Map.of(
                "success", false,
                "message", "Invalid payload."
            ));
        }

        int sum = 0;
        boolean done = false;

        while (!done) {
            Response<DocumentList> response = database.listDocuments(
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

            for (var document : result.getDocuments()) {
                Response<Void> deleteResponse = database.deleteDocument(
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

        return res.json(Map.of(
            "success", true,
            "sum", sum
        ));
        } catch (AppwriteException e) {
            return res.json(Map.of(
                "success", false,
                "message", "Unexpected error: " + e.getMessage()
            ));
        }
}

