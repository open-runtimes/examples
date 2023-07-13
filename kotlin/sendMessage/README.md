
# SendMessage()

 A Kotlin Cloud Function for sending a message using a specific channel to a receiver

  

Supported channels are `SMS`, `Email` , `Disocrd`, and `Twitter`.

  

_SMS Example payload_

  

```json
{ 
    "type": "SMS", 
    "receiver": "+123456789", 
    "message": "Programming is fun!" 
}
```

  

_Email Example payload_

  

```json
{
    "type": "Email",
    "receiver": "hello@example.com",
    "message": "Programming is fun!",
    "subject": "Programming is funny!"
}
```

  

_Discord Example payload_

  

```json

{
    "type": "Discord",
    "message": "Hi"
}
```

_Twitter Example payload_

  

```json
{
    "type": "Twitter",
    "message": "Programming is fun!"
}
```

  

_Successful function response:_

  

```json
{   
    "success": true 
}
```

  

_Error function response:_

  

```json
{
    "success": false,
    "message": "Failed to send message,check webhook URL"
}
```

## 📝 Variables

  

List of variables used by this cloud function:

  

Mailgun

  

-  **MAILGUN_API_KEY** - API Key for Mailgun
-  **MAILGUN_DOMAIN** - Domain Name from Mailgun

  

Discord

  

-  **DISCORD_WEBHOOK_URL** - Webhook URL for Discord

  

Twilio

  

-  **TWILIO_ACCOUNT_SID** - Acount SID from Twilio
-  **TWILIO_AUTH_TOKEN** - Auth Token from Twilio
-  **TWILIO_SENDER** - Sender Phone Number from Twilio

Twitter
-  **TWITTER_API_KEY** - API Key for Twitter
-  **TWITTER_API_KEY_SECRET** - API Key Secret for Twitter
-  **TWITTER_ACCESS_TOKEN** - Access Token from Twitter
-  **TWITTER_ACCESS_TOKEN_SECRET** - Access Token Secret from Twitter
  

## 🚀 Deployment

  

1. Clone this repository, and enter this function folder:

  

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd kotlin/sendMessage
```

  

2. Enter this function folder and build the code:

  

```bash
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=Index.kt --rm --interactive --tty --volume $PWD:/usr/code openruntimes/kotlin:v2-1.6 sh /usr/local/src/build.sh
```

  

As a result, a `code.tar.gz` file will be generated.

  

3. Spin-up open-runtime:

  

```bash
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/kotlin:v2-1.6 sh /usr/local/src/start.sh
```

  

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Kotlin runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/kotlin-1.6/example).

  

4. In new terminal window, execute the function. Example curl command (Email):

  

```bash
curl -X POST http://localhost:3000/ -d '{"variables": {"MAILGUN_API_KEY":"YOUR_MAILGUN_API_KEY","MAILGUN_DOMAIN":"YOUR_MAILGUN_DOMAIN"},"payload": "{\"type\": \"Email\",\"receiver\": \"hello@example.com\",\"message\": \"Programming is fun!\",\"subject\": \"Programming is funny!\"}"}' -H "X-Internal-Challenge: secret-key" -H  "Content-Type: application/json"
```

  

## 📝 Notes

  

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
