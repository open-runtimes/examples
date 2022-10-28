using System.Net;
using Newtonsoft.Json;
using WipeAppwriteBucket.Models;

namespace WipeAppwriteBucket
{
    class Program
    {
        static string APPWRITE_FUNCTION_PROJECT_ID = "";
        static string APPWRITE_FUNCTION_API_KEY = "";
        static string baseUrl = "http://localhost/v1";
        static string bucketId = "";
        static async Task Main(string[] args)
        {
            try
            {
                var response = await GetFilesFromBucketByBucketId(bucketId);
                //String replace is used as $id property was not getting deserialized
                var output = response.Replace("\"$id\"", "\"id\"");
                var payload = JsonConvert.DeserializeObject<ApiResponse>(output);
                await WipeAppWriteBucket(payload);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }

        
        public static async Task<string> GetFilesFromBucketByBucketId(string bucketId)
        {
            var client = new HttpClient();
            client.DefaultRequestHeaders.Add("X-Appwrite-Project", APPWRITE_FUNCTION_PROJECT_ID);
            client.DefaultRequestHeaders.Add("X-Appwrite-key", APPWRITE_FUNCTION_API_KEY);

            //Get Files from the Bucket
            return await client.GetStringAsync($"{baseUrl}/storage/buckets/{bucketId}/files");
        }

        public static async Task<HttpResponseMessage> WipeAppWriteBucket(ApiResponse payload)
        {

            var client = new HttpClient();
            client.DefaultRequestHeaders.Add("X-Appwrite-Project", APPWRITE_FUNCTION_PROJECT_ID);
            client.DefaultRequestHeaders.Add("X-Appwrite-key", APPWRITE_FUNCTION_API_KEY);

            if (payload != null && payload.Files != null && payload?.Files.Count > 0)
            {
                foreach (var file in payload.Files)
                {
                    await client.DeleteAsync($"{baseUrl}/storage/buckets/{bucketId}/files/{file.id}");
                }
                return new HttpResponseMessage(HttpStatusCode.OK);
            }
            else
            {
                return new HttpResponseMessage(HttpStatusCode.BadRequest);
            }

        }

    }
}
