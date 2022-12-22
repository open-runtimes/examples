using Newtonsoft.Json;

namespace wipeAppwriteCollection.Models;

public class ListDocumentsResponse
{
    [JsonProperty("total")]
    public int Total { get; set; }

    [JsonProperty("documents")]
    public Document[] Documents { get; set; }
}
