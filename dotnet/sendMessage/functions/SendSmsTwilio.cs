using System.Text;

namespace sendMessage.functions;
public class SendSmsTwilio
{
    public static async Task<Dictionary<string, object>> SendSMS(Dictionary<string, string> variables, string phoneNumber, string message)
    {
        if (string.IsNullOrEmpty(phoneNumber))
        {
            throw new Exception("No phone number provided");
        }

        if (string.IsNullOrEmpty(message))
        {
            throw new Exception("No message provided");
        }

        string accountSID = variables.TryGetValue("TWILIO_ACCOUNT_SID", out string accountSIDValue) ? accountSIDValue : null;
        string authToken = variables.TryGetValue("TWILIO_AUTH_TOKEN", out string authTokenValue) ? authTokenValue : null;
        string sender = variables.TryGetValue("TWILIO_SENDER", out string senderValue) ? senderValue : null;

        if (string.IsNullOrEmpty(accountSID))
        {
            throw new Exception("Missing Twilio account SID");
        }

        if (string.IsNullOrEmpty(authToken))
        {
            throw new Exception("Missing Twilio auth token");
        }

        if (string.IsNullOrEmpty(sender))
        {
            throw new Exception("Missing Twilio sender");
        }

        try
        {
            using (var httpClient = new HttpClient())
            {
                httpClient.DefaultRequestHeaders.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Basic", Convert.ToBase64String(Encoding.ASCII.GetBytes($"{accountSID}:{authToken}")));

                var values = new Dictionary<string, string>
                {
                    { "From", sender },
                    { "To", phoneNumber },
                    { "Body", message }
                };

                var content = new FormUrlEncodedContent(values);

                var response = await httpClient.PostAsync($"https://api.twilio.com/2010-04-01/Accounts/{accountSID}/Messages.json", content);
                response.EnsureSuccessStatusCode();
                var responseString = await response.Content.ReadAsStringAsync();

              
                Console.WriteLine(responseString);
            }
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return new Dictionary<string,object>  {{ "success", false}, {"message", e.Message }};
        }

        return new Dictionary<string, object> {{ "success" , true }, {"message", "Your message was sent" }};
    }
}

