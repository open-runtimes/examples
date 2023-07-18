using System;
using System.Collections.Specialized;
using System.Net;
using System.Text;

public class TwilioSMS
{
    public static void SendSMS(string accountSID, string authToken, string sender, string phoneNumber, string message)
    {
        if (string.IsNullOrEmpty(phoneNumber))
        {
            throw new Exception("No phone number provided");
        }

        if (string.IsNullOrEmpty(message))
        {
            throw new Exception("No message provided");
        }

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
            using (var client = new WebClient())
            {
                client.Credentials = new NetworkCredential(accountSID, authToken);

                var data = new NameValueCollection
                {
                    { "From", sender },
                    { "To", phoneNumber },
                    { "Body", message }
                };

                var response = client.UploadValues($"https://api.twilio.com/2010-04-01/Accounts/{accountSID}/Messages.json", data);
                var responseString = Encoding.UTF8.GetString(response);

                // Handle the response if needed
                Console.WriteLine(responseString);
            }
        }
        catch (Exception e)
        {
            Console.WriteLine(e.Message);
            throw;
        }
    }
}

public class Program
{
    public static void Main()
    {
        string accountSID = "";
        string authToken = "";
        string sender = "";
        string phoneNumber = "";
        string message = "";

        try
        {
            TwilioSMS.SendSMS(accountSID, authToken, sender, phoneNumber, message);
            Console.WriteLine("SMS sent successfully.");
        }
        catch (Exception ex)
        {
            Console.WriteLine("SMS sending failed: " + ex.Message);
        }
    }
}
