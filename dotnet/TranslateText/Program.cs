using System.Text;
using Amazon.Translate;
using Amazon.Translate.Model;
using Newtonsoft.Json;


class RequestPayload
{
    public string? Provider { get; set; }
    public string? From { get; set; }
    public string? To { get; set; }
    public string? Text { get; set; }
}
class Program
{
    private static readonly string key = "<your-translator-key>";
    private static readonly string endpoint = "https://api.cognitive.microsofttranslator.com";
    private static readonly string payload = "{\"provider\":\"aws\",\"from\":\"cs\",\"to\":\"en\",\"text\":\"Ahoj svet.\"}";
    // location, also known as region.
    // required if you're using a multi-service or regional (not global) resource. It can be found in the Azure portal on the Keys and Endpoint page.
    private static readonly string location = "<YOUR-RESOURCE-LOCATION>";

    static async Task Main(string[] args)
    {
        try
        {
            var requestPayload = JsonConvert.DeserializeObject<RequestPayload>(payload);
            if (requestPayload?.Provider == "azure")
            {
                await TranslateUsingAzure(requestPayload.Provider, requestPayload.From, requestPayload.To, requestPayload.Text);
            }
            else if (requestPayload?.Provider == "aws")
            {
                await TranslateUsingAws(requestPayload.Provider, requestPayload.From, requestPayload.To, requestPayload.Text);
            }

        }
        catch(Exception ex)
        {
            Console.WriteLine(ex.Message);
        }
       
        

    }


    private async static Task<string> TranslateUsingAzure(string provider,string from,string to,string text)
    {
        // Input and output languages are defined as parameters.
        string route = $"/translate?api-version=3.0&from={from}&to={to}";

        object[] body = new object[] { new { Text = text } };
        var requestBody = JsonConvert.SerializeObject(body);

        using (var client = new HttpClient())
        using (var request = new HttpRequestMessage())
        {
            // Build the request.
            request.Method = HttpMethod.Post;
            request.RequestUri = new Uri(endpoint + route);
            request.Content = new StringContent(requestBody, Encoding.UTF8, "application/json");
            request.Headers.Add("Ocp-Apim-Subscription-Key", key);
            // location required if you're using a multi-service or regional (not global) resource.
            request.Headers.Add("Ocp-Apim-Subscription-Region", location);
            // Send the request and get response.
            HttpResponseMessage response = await client.SendAsync(request).ConfigureAwait(false);
            // Read response as a string.
            string result = await response.Content.ReadAsStringAsync();
            Console.WriteLine(result);
            return result;
        }
    }

    private async static Task<string> TranslateUsingAws(string provider, string from, string to, string text)
    {
        var awsCredentials = new Amazon.Runtime.BasicAWSCredentials("myaccesskey", "mysecretkey");

        var client = new AmazonTranslateClient(credentials: awsCredentials, region : Amazon.RegionEndpoint.EUWest2);

        // Set the source language to "auto" to request Amazon Translate to
        // automatically detect te language of the source text.

        var request = new TranslateTextRequest
        {
            SourceLanguageCode = from,
            TargetLanguageCode = to,
            Text = text,
        };

        var response = await client.TranslateTextAsync(request);

        return response.TranslatedText;
    }
}