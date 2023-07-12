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
            using (HttpClient client = new HttpClient())
            {
                var content = new StringContent(
                    "{\"content\": \"" + message + "\"}",
                    Encoding.UTF8,
                    "application/json"
                )
            }

            HttpResponseMessage response = await client.PostAsync(webhook, content);
        }
        catch (Exception e)
        {
            Console.Writeline(e);
            return new Dictionary<string, object>{{"success", false}, {"message", e.Message}};
        }

        return new Dictionary<string, object>{{"success" , true}};
}
}