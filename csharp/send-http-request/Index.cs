using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;

static readonly HttpClient http = new HttpClient();

public async Task<RuntimeResponse> Main(RuntimeRequest req, RuntimeResponse res)
{
    string url = null;
    string method = null;
    string body = null;
    string contentType = null;
    Dictionary<string,string> headers = null;
    if (!string.IsNullOrEmpty(req.Payload))
    {
        var payload = JsonConvert.DeserializeObject<Dictionary<string, object>>(req.Payload, settings: null);
        url = !payload.ContainsKey("url") ? null : (string)payload["url"];
        method = !payload.ContainsKey("method") ? null : (string)payload["method"];
        contentType = !payload.ContainsKey("content_type") ? null : (string)payload["content_type"];
        body = !payload.ContainsKey("body") ? null : payload["body"].ToString();
        headers = JsonConvert.DeserializeObject<Dictionary<string, string>>(payload.ContainsKey("headers") ? payload["headers"].ToString() : "");
    }

    // Check for input variable correction
    if (url == null || method == null)
    {
        return res.Json(new()
        {
            { "success", false },
            { "message", "Custom data is in incorrect format or don't exist" }
        });
    }

    if (!method.Equals("GET") && !method.Equals("POST") && !method.Equals("PUT") &&
        !method.Equals("DELETE") && !method.Equals("HEAD") && !method.Equals("OPTIONS") &&
        !method.Equals("TRACE"))
    {
        return res.Json(new()
            {
                { "success", false },
                { "message", "HTTP method is in inccorect format" }
            });
    }

    var request = (HttpWebRequest)WebRequest.Create(url);
    request.Method = method;

    if (headers != null)
    {
        foreach(string key in headers.Keys)
        {
            request.Headers.Add(key, headers[key]);
        }
    }

    if(body != null)
    {
        request.ContentType = contentType;
        var streamWriter = new StreamWriter(request.GetRequestStream());
        streamWriter.Write(body);
    }

    try
    {
        var httpResponse = (HttpWebResponse)request.GetResponse();
        var streamReader = new StreamReader(httpResponse.GetResponseStream());
        var result = streamReader.ReadToEnd();
        return res.Json(new()
            {
                { "success", true },
                { "response", result}
            });
    }
    catch (Exception ex)
    {
        return res.Json(new()
            {
                { "success", false },
                { "response", ex.Message }
            });
    }
}