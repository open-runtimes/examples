using System.Text;


namespace SendMessageFunction
{
public class DiscordWebhook{
    public static async Task<Dictionary<string, object>> SendDiscordMessage(Dictionary<string,string> variables, string? message)
    {
        if(message == null)
        {
            return new Dictionary<string, object> {{"success", false}, {"message","Missing message"}};
        }

        string webhook = variables["DISCORD_WEBHOOK_URL"];
    
        if(webhook == null)
        {
            return new Dictionary<string, object> {{"success", false}, {"message","Missing webhook"}};
        }

        try{
            using (HttpClient httpclient = new HttpClient()){
                 var contentofmessage = new StringContent(
                    "{\"content\": \"" + message + "\"}",
                    Encoding.UTF8,
                    "application/json"
                );
                HttpResponseMessage response = await httpclient.PostAsync(webhook, contentofmessage);
                response.EnsureSuccessStatusCode();
                return new Dictionary<string, object> {{"success" , true}, {"message", "Your message was sent"}};
            }
            
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return new Dictionary<string, object> {{"success", false}, {"message", e.ToString()}};
        }

        
}
}
}