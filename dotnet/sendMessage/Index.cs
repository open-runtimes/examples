using sendMessage.functions;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Net;
using System.Runtime;
using Newtonsoft.Json;
using System.Runtime.InteropServices;
using Tweetinvi.Streams.Model;
using Tweetinvi.Core.Models;

async Task Main(RuntimeRequest req, RuntimeResponse res) 
{
    Dictionary<string, object> result = new Dictionary<string, object>();
    try{
    if(String.IsNullOrEmpty(req.Payload)){
        return res.Json(new()
        {
            { "success", false },
            { "message", "Payload is missing" }
        });
    }
    var payload = JsonConvert.DeserializeObject<Dictionary<string, string>>(req.Payload);
    string message = payload["message"];
    string channel = payload["type"];

    if(channel == "SMS"){
            string phoneNumber = payload["receiver"];
            result = await SendSmsTwilio.SendSMS(req.Variables, phoneNumber, message);
    }
    else if(channel == "Email"){
            string recipient = payload["receiver"];
            string subject = payload["subject"];
            result = await Mail.SendMail(req.Variables, recipient, message, subject);
    }
    else if(channel == "Twitter"){
        result = await TwitterSender.SendTweet(req.Variables, message);
    }
    else if(channel == "Discord"){
            result = await DiscordWebhook.SendDiscordMessage(req.Variables, message);

    }
    }
    catch (Exception e){
        return res.Json(new()
        {
            { "success", false },
            { "message", e.Message }
        });
    }

    return res.Json(
        result
    );

}
