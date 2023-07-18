using System;
using System.Collections.Generic;
using System.Net;
using System.Text;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace sendMessage.functions;

public class DiscordWebhook{
    public static async Task<object> SendDiscordMessage(Dictionary<string,string> variables, string message)
    {
        if(message == null)
        {
            throw new Exception("Missing message");
        }

        string webhook = variables["DISCORD_WEBHOOK_URL"];
    
        if(webhook == null)
        {
            throw new Exception("Missing Discord webhook");
        }

        try{
            using (HttpClient httpclient = new HttpClient()){
                 var contentofmessage = new StringContent(
                    "{\"content\": \"" + message + "\"}",
                    Encoding.UTF8,
                    "application/json"
                );
                HttpResponseMessage response = await httpclient.PostAsync(webhook, contentofmessage);
            }
            
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return new Dictionary<string, object>{{"success", false}, {"message", e.Message}};
        }

        return new Dictionary<string, object>{{"success" , true}};
}
}