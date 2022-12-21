using Newtonsoft.Json;
using wipeAppwriteCollection;
using wipeAppwriteCollection.Models;

public async Task<RuntimeResponse> Main(RuntimeRequest req, RuntimeResponse res)
{
    var baseUrl = Environment.GetEnvironmentVariable("APPWRITE_FUNCTION_ENDPOINT");
    var apiKey = Environment.GetEnvironmentVariable("APPWRITE_FUNCTION_API_KEY");
    var projectId = Environment.GetEnvironmentVariable("APPWRITE_FUNCTION_PROJECT_ID");

    if (string.IsNullOrWhiteSpace(baseUrl) ||
        string.IsNullOrWhiteSpace(apiKey) ||
        string.IsNullOrWhiteSpace(projectId))
    {
        res.Json(new() {
            { "success", false },
            { "message", "Please provide all required environment variables." }
        });
        return res;
    }

    var reqBody = JsonConvert.DeserializeObject<Dictionary<string, string>>(req.Payload);
    var gotRequestVariables =
        reqBody.TryGetValue("databaseId", out string databaseId) &
        reqBody.TryGetValue("collectionId", out string collectionId);
    if (!gotRequestVariables)
    {
        res.Json(new() {
            { "success", false },
            { "message", "Payload is invalid." }
        });
        return res;
    }

    var databaseClient = new AppwriteDatabaseClient(baseUrl, apiKey, projectId);
    ListDocumentsResponse documents;
    var done = false;
    while (!done)
    {
        documents = await databaseClient.ListDocuments(databaseId, collectionId);
        if (documents?.Documents == null)
        {
            res.Json(new() {
                { "success", false },
                { "message", "Failed to get documents from database." }
            });
            return res;
        }

        foreach (var document in documents.Documents)
        {
            await databaseClient.DeleteDocument(databaseId, collectionId, document.Id);
        }

        if (documents.Total <= 0)
        {
            done = true;
        }
    }

    res.Json(new() {
        { "success", true }
    });
    return res;
}
