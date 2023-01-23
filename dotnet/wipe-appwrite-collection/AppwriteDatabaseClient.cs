using Newtonsoft.Json;
using wipeAppwriteCollection.Models;

namespace wipeAppwriteCollection;

public class AppwriteDatabaseClient
{
    private readonly HttpClient httpClient;

    public AppwriteDatabaseClient(string baseUrl, string apiKey, string projectId)
    {
        httpClient = new HttpClient
        {
            BaseAddress = new Uri(baseUrl),
        };

        httpClient.DefaultRequestHeaders.Add("X-Appwrite-Key", apiKey);
        httpClient.DefaultRequestHeaders.Add("X-Appwrite-Project", projectId);
    }

    public async Task<ListDocumentsResponse> ListDocuments(string databaseId, string collectionId, int? limit = 25)
    {
        var response = await httpClient.GetAsync($"/v1/databases/{databaseId}/collections/{collectionId}/documents?limit={limit}");
        var responseStr = await response.Content.ReadAsStringAsync();
        var parsedResponse = JsonConvert.DeserializeObject<ListDocumentsResponse>(responseStr);
        return parsedResponse;
    }

    public async Task DeleteDocument(string databaseId, string collectionId, string documentId)
    {
        await httpClient.DeleteAsync($"/v1/databases/{databaseId}/collections/{collectionId}/documents/{documentId}");
    }
}
