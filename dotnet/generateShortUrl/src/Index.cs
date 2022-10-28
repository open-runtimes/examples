using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

public async Task<RuntimeResponse> Main(RuntimeRequest req, RuntimeResponse res)
{
  try
  {
    // Check if payload is present

    if(String.IsNullOrEmpty(req.Payload))
    {
      return res.Json(new()
      {
        { "success", false },
        { "message", "Payload is missing" }
      });
    }

    var payload = JsonConvert.DeserializeObject<Dictionary<string, string>>(req.Payload);

    // Check if provider and URL to be shortened are present in the payload

    if(!(payload.ContainsKey("provider") && payload.ContainsKey("url")))
    {
      return res.Json(new()
      {
        { "success", false },
        { "message", "Payload must contain a provider and a URL" }
      });
    }

    string provider = payload["provider"];
    string url = payload["url"];

    string apiKey = "";
    string uri = "";
    Dictionary<string, string> body = new Dictionary<string, string>();
    string json = "";

    // Preparing API info based on chosen provider

    if(payload["provider"].Equals("bitly"))
    {
      apiKey = req.Variables["BITLY_API_KEY"];
      uri = "https://api-ssl.bitly.com/v4/shorten";
      body.Add("long_url", url);
      json = JsonConvert.SerializeObject(body);
    }

    else if(payload["provider"].Equals("tinyurl"))
    {
      apiKey = req.Variables["TINYURL_API_KEY"];
      uri = "https://api.tinyurl.com/create";
      body.Add("url", url);
      json = JsonConvert.SerializeObject(body);
    }

    else
    {
      return res.Json(new()
        {
          { "success", false },
          { "message", "Provider is invalid" }
      });
    }

    // Sending Http Request to the chosen provider's API

    var client = new HttpClient();
    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", apiKey);
    var data = new StringContent(json, Encoding.UTF8, "application/json");
    var httpResponse = await client.PostAsync(uri, data);
    var responseContent = await httpResponse.Content.ReadAsStringAsync();
    var response = JsonConvert.DeserializeObject<dynamic>(responseContent);

    // Successful response

    if(httpResponse.StatusCode == HttpStatusCode.OK || httpResponse.StatusCode == HttpStatusCode.Created)
    {
      if(payload["provider"].Equals("bitly"))
      {
        return res.Json(new()
        {
            { "success", true },
            { "url", response?.link.ToString()  }
        });
      }
      else if(payload["provider"].Equals("tinyurl"))
      {
        return res.Json(new()
        {
          { "success", true },
          { "url", response?.data.tiny_url.ToString() }
        });
      }
    }

    // Failed response

    else
    {
      if(payload["provider"].Equals("bitly"))
      {
        return res.Json(new()
        {
          { "success", false },
          { "message", response?.description.ToString() }
        });
      }
      else if(payload["provider"].Equals("tinyurl"))
      {
        return res.Json(new()
        {
          { "success", false },
          { "message", response?.errors[0].ToString() }
        });
      }  
    }
  }
  catch(Exception ex)
  {
    return res.Json(new()
    {
      { "success", false },
      { "message", ex.Message }
    });
  }

  return res.Json(new()
  {
    { "success", false },
    { "message", "Function failed for unknown reasons" }
  });
}