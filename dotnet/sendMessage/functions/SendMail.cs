using System.Text;
using System.Net.Http.Headers;


namespace SendMessage.Functions
{

public class Mail
{
    public static async Task<Dictionary<string,object>> SendMail(Dictionary<string,string> variables, string? email, string? message, string? subject)
    {
        if(email == null || message == null || subject == null)
        {
            return new Dictionary<string,object> {{"success", false}, {"message","Missing email, subject, or message"}};
        }

        string domain = variables["MAILGUN_DOMAIN"];
        string apiKey = variables["MAILGUN_API_KEY"];
        HttpResponseMessage? response;
    
        if(domain == null)
        {
            return new Dictionary<string,object> {{"success", false}, {"message","Missing Mailgun domain"}};
        }
        if(apiKey == null)
        {
            return new Dictionary<string,object> {{"success", false}, {"message","Missing Mailgun API key"}};
        }
    try 
        {
        using (HttpClient client = new HttpClient())
        {
            client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", Convert.ToBase64String(Encoding.ASCII.GetBytes($"api:{apiKey}")));

            var content = new FormUrlEncodedContent(new Dictionary<string, string>
            {
                {"from", "<welcome@my-awesome-app.io>"},
                {"to", email},
                {"subject", subject},
                {"text", message}
            });

            response = await client.PostAsync($"https://api.mailgun.net/v3/{domain}/messages", content);
            response.EnsureSuccessStatusCode();
            var responseString = await response.Content.ReadAsStringAsync();
            return new Dictionary<string, object> {{"success" , true}, {"message", "Your message was sent"}};
        }       
           
        }
    catch (Exception e)
        {
            return new Dictionary<string, object> {{"success" , false}, {"message" , e.ToString()}};
        }

        
    }
}
}