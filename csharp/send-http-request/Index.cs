using Newtonsoft.Json;
using System.Threading.Tasks;
using System.Net.Http;
using System;
using System.Collections.Generic;

static readonly HttpClient http = new HttpClient();

public async Task<RuntimeResponse> Main(RuntimeRequest req, RuntimeResponse res)
{
    string url = null;
    string method = null;
    Dictionary<string,string> body = null;
    Dictionary<string,string> headers = null;
    if (!string.IsNullOrEmpty(req.Payload))
    {
        var payload = JsonConvert.DeserializeObject<Dictionary<string, object>>(req.Payload, settings: null);
        url = payload["url"] == null ? null : (string)payload["url"];
        method = payload["method"] == null ? null : (string)payload["method"];
        body = JsonConvert.DeserializeObject<Dictionary<string, string>>(payload.ContainsKey("body") ? payload["body"].ToString() : "" );
        headers = JsonConvert.DeserializeObject<Dictionary<string, string>>(payload.ContainsKey("headers") ? payload["headers"].ToString() : "");
    }

    if (url == null || method == null)
    {
        return res.Json(new()
        {
            { "success", false },
            { "message", "Custom data is in incorrect format or don't exist" }
        });
    }

    var request = new HttpRequestMessage
    {
        Method = HttpMethod.Get,
        RequestUri = new Uri(url)
    };

    switch (method)
    {
        case "GET":
            request.Method = HttpMethod.Get;
            break;
        case "POST":
            request.Method = HttpMethod.Post;
            break;
        case "PUT":
            request.Method = HttpMethod.Put;
            break;
        case "DELETE":
            request.Method = HttpMethod.Delete;
            break;
        case "HEAD":
            request.Method = HttpMethod.Head;
            break;
        case "OPTIONS":
            request.Method = HttpMethod.Options;
            break;
        case "TRACE":
            request.Method = HttpMethod.Trace;
            break;
        default:
            return res.Json(new()
            {
                { "success", false },
                { "message", "HTTP method is in inccorect format" }
            });
    }

    if(headers != null)
    {
        foreach(string key in headers.Keys)
        {
            request.Headers.Add(key, headers[key]);
        }
    }

    if(body != null)
    {
        request.Content = new FormUrlEncodedContent(body);
    }

    var response = await http.SendAsync(request);
    if (response.IsSuccessStatusCode)
    {
        string responseString = await response.Content.ReadAsStringAsync();
        return res.Json(new()
        {
            { "success", true },
            { "response", responseString}
        });
    }
    return res.Json(new()
    {
        { "success", false },
        { "message", response.ReasonPhrase }
    });
}