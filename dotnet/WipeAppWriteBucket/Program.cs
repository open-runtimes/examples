using System;
using System.Net;
using Appwrite;
using Newtonsoft.Json;
namespace WipeAppWriteBucket{

    class Payload
    {
        public string? BucketId { get; set; }
    }

    class AppWriteFile
    {
        public Guid Id { get; set; }
        public Guid BucketId { get; set; }
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
    class Program{
        static async Task Main(string[] args){

            string APPWRITE_FUNCTION_PROJECT_ID = "";

            string APPWRITE_FUNCTION_ENDPOINT = "";

            string APPWRITE_FUNCTION_API_KEY = "";

            try
            {
                await WipeAppWriteBucket(APPWRITE_FUNCTION_ENDPOINT, APPWRITE_FUNCTION_PROJECT_ID, APPWRITE_FUNCTION_API_KEY);
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }


        public static async Task<HttpResponseMessage> WipeAppWriteBucket(string endpoint,string projectId,string key)
        {
            var client = new Client();

            client.SetEndPoint(endpoint) // Make sure your endpoint is accessible
                  .SetProject(projectId) // Your project ID
                  .SetKey(key);

            var storage = new Storage(client);
            try
            {
                var request = "{ \"bucketId\":\"profilePictures\"}";
                var payload = JsonConvert.DeserializeObject<Payload>(request);

                //List files in a bucket
                var response = await storage.ListFiles(search: payload?.BucketId, limit: 25, offset: 0, orderType: OrderType.ASC);

                var responseContent = await response.Content.ReadAsStringAsync();
                var result = JsonConvert.DeserializeObject<Result>(responseContent);

                var files = result?.Files;

                foreach(var file in files ?? new List<AppWriteFile>())
                {
                    await storage.DeleteFile(file.Id.ToString());
                }

                return new HttpResponseMessage(HttpStatusCode.OK);
            }
            catch (AppwriteException e)
            {
                Console.WriteLine(e.Message);
                return new HttpResponseMessage(HttpStatusCode.BadRequest);

            }

        }
        
    }
}