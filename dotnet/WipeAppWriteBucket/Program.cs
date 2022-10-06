using System;
using System.Net;
using Appwrite;
using Newtonsoft.Json;
namespace WipeAppWriteBucket
{

    class Payload
    {
        public string? BucketId { get; set; }
    }

    class AppWriteFile
    {
        public string? Id { get; set; }
        public string? BucketId { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime UpdatedAt { get; set; }
        public string? Name { get; set; }
        public string[]? Permissions { get; set; }
        public Guid Signature { get; set; }
        public string? MimeType { get; set; }
        public int OriginalSize { get; set; }
        public int TotalChunks { get; set; }
        public int ChunksUploaded { get; set; }
    }

    class Result
    {
        public int Total { get; set; }
        public List<AppWriteFile>? Files { get; set; }
    }
    class Program
    {
        static async Task Main(string[] args)
        {

            string APPWRITE_FUNCTION_PROJECT_ID = "";
            string APPWRITE_FUNCTION_API_KEY = "";
            string baseUrl = "http://localhost/v1";
            var bucketId = "";
            try
            {
                var client = new HttpClient();
                client.DefaultRequestHeaders.Add("X-Appwrite-Project", APPWRITE_FUNCTION_PROJECT_ID);
                client.DefaultRequestHeaders.Add("X-Appwrite-key", APPWRITE_FUNCTION_API_KEY);

                //Get Files from the Bucket
                var response = await client.GetStringAsync($"{baseUrl}/storage/buckets/{bucketId}/files");
                var payload = JsonConvert.DeserializeObject<Result>(response);

                if (payload != null && payload.Files != null && payload?.Files.Count > 0)
                {
                    foreach (var file in payload.Files)
                    {
                        //Delete Files from the Bucket
                        var url = $"{baseUrl}/storage/buckets/{bucketId}/files/{file.Id}";
                        await client.DeleteAsync(url);
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }




    }
}
