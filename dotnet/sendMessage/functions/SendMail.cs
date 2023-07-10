using System;
using System.Collections.Generic;
using System.Net;
using System.Text;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace sendMessage.functions;


public class Mail
{
    public static async Task<object> SendMail(Dictionary<string,string> variables, string email, string message, string subject)
    {
        if(email == null || message == null || subject == null)
        {
            throw new Exception("Missing email, message or subject");
        }

        string domain = variables["MAILGUN_DOMAIN"];
        string apiKey = variables["MAILGUN_API_KEY"];
    
        if(domain == null)
        {
            throw new Exception("Missing Mailgun domain");
        }
        if(apiKey == null)
        {
            throw new Exception("Missing Mailgun API key");
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

            HttpResponseMessage response = await client.PostAsync($"https://api.mailgun.net/v3/{domain}/messages", content);

        }       
           
        }
    catch (Exception e)
        {
            Console.WriteLine(e);
            return new Dictionary<string, object>{{"success" , false}, {"message" , e.Message}};
        }

        return new Dictionary<string, object>{{"success" , true}};
    }
}