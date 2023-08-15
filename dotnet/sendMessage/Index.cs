using SendMessage.functions;
using Newtonsoft.Json;
using System.Text.Json;

namespace DotNetRuntime {

    public class Handler {

public async Task<RuntimeOutput> Main(RuntimeContext Context) 
{
    Dictionary<string, object> result = new Dictionary<string, object>();

    string? channel = "type";
    string? message = "";
    string? recipient = "";
    string? subject = "";
    object? response = true;
    object? responsemessage = "";
    Dictionary<string,string>? variables = new Dictionary<string,string>();
    Dictionary<string,string>? payload = new Dictionary<string,string>();
    


    if(!(Context.Req.Body is string)){

        object? variableobject;
        object? payloadobject;
    
        Dictionary<string,object> body = (Dictionary<string, object>) Context.Req.Body;
        body.TryGetValue("variables", out variableobject);
        body.TryGetValue("payload", out payloadobject);
        string? tempstring = ((JsonElement) variableobject).ToString();
        variables = JsonConvert.DeserializeObject<Dictionary<string,string>>(tempstring);
        tempstring = ((JsonElement) payloadobject).ToString();
        payload = JsonConvert.DeserializeObject<Dictionary<string,string>>(tempstring);

        payload.TryGetValue("type", out channel);
        payload.TryGetValue("message", out message);
        payload.TryGetValue("receiver", out recipient);     

     if(channel == "SMS"){
             result = await SendSmsTwilio.SendSMS(variables, recipient, message);
     }
     else if(channel == "Email"){
            payload.TryGetValue("subject", out subject);
             result = await Mail.SendMail(variables, recipient, message, subject);
     }
     else if(channel == "Twitter"){
         result = await TwitterSender.SendTweet(variables, message);
     }
     else if(channel == "Discord"){
             result = await DiscordWebhook.SendDiscordMessage(variables, message);
            
     }
        
    }

    result.TryGetValue("success", out response);
    result.TryGetValue("message", out responsemessage);

     return Context.Res.Json( 
        new Dictionary<string, object>(){{"success", response},{"message", responsemessage}});

}
}
}