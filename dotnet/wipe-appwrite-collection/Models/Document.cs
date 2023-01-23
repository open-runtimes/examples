using Newtonsoft.Json;

namespace wipeAppwriteCollection.Models;

public class Document
{
    [JsonProperty("$id")]
    public string Id { get; set; }
}
