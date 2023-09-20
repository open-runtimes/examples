using Newtonsoft.Json;
using SendMessageFunction;

public async Task<RuntimeResponse> Main(RuntimeRequest req, RuntimeResponse res)
{
    string? channel = "type";
    string? message = "";
    string? recipient = "";
    string? subject = "";
    object response = true;
    object responsemessage = "";
    Dictionary<string, object> result = new Dictionary<string, object>();

    if (string.IsNullOrEmpty(req.Payload))
    {
      return res.Json(new()
      {
        {"success", false},
        {"message", req.Variables}
      });
    }

    var payload = JsonConvert.DeserializeObject<Dictionary<string, string>>(req.Payload);
    payload.TryGetValue("type", out channel);
    payload.TryGetValue("message", out message);
    payload.TryGetValue("receiver", out recipient);

    if (channel == "Discord"){
        result = await DiscordWebhook.SendDiscordMessage(req.Variables, message);
    }
    else if(channel == "Email"){
        payload.TryGetValue("subject", out subject);
        result = await Mail.SendMail(req.Variables, recipient, message, subject);
    }
    else if(channel == "Twitter"){
        result = await TwitterSender.SendTweet(req.Variables, message);
    }
    else if(channel == "SMS"){
        result = await SendSmsTwilio.SendSMS(req.Variables, recipient, message);
    }

    result.TryGetValue("success", out response!);
    result.TryGetValue("message", out responsemessage!);

    return res.Json(new()
    {
        {"success", response},
        {"message", responsemessage}
    });
}
