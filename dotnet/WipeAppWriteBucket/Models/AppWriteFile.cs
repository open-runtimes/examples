using Newtonsoft.Json;

namespace WipeAppWriteBucket.Models
{
    class AppWriteFile
    {
        [JsonProperty(PropertyName = "id")]
        public string id { get; set; }
        public string? BucketId { get; set; }
        [JsonProperty("$createdAt")]
        public DateTime CreatedAt { get; set; }
        [JsonProperty("$updatedAt")]
        public DateTime UpdatedAt { get; set; }
        public string? Name { get; set; }
        [JsonProperty("$permissions")]
        public string[]? Permissions { get; set; }
        public Guid Signature { get; set; }
        public string? MimeType { get; set; }
        [JsonProperty("sizeOriginal")]
        public int OriginalSize { get; set; }
        [JsonProperty("chunksTotal")]
        public int TotalChunks { get; set; }
        [JsonProperty("chunksUploaded")]
        public int ChunksUploaded { get; set; }
    }
}

