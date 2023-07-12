
# SendMessage()

  

A Kotlin Cloud Function for sending a message using a specific channel to a receiver

  

Supported channels are `SMS`, `Email` , and `Discord`.

  

_SMS Example payload_

  

```json

{ "type": "SMS", "receiver": "+123456789", "message": "Programming is fun!" }

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

  

## 🚀 Deployment

  

1. Clone this repository, and enter this function folder:

  

```

$ git clone https://github.com/open-runtimes/examples.git && cd examples

$ cd kotlin/sendMessage

```

  

2. Enter this function folder and build the code:

  

```bash

docker  run  --rm  --interactive  --tty  --volume  $PWD:/usr/code  openruntimes/kotlin:v1.6.0  sh  /usr/local/src/build.kotlin

```

  

As a result, a `kotlin.tar.gz` file will be generated.

  

3. Start the Open Runtime:

  

```bash

docker  run  -p  3000:3000  -e  INTERNAL_RUNTIME_KEY=secret-key  -e  INTERNAL_RUNTIME_ENTRYPOINT=main.kt  --rm  --interactive  --tty  --volume  $PWD/kotlin.tar.gz:/tmp/kotlin.tar.gz:ro  openruntimes/kotlin:v1.6.0  sh  /usr/local/src/start.kotlin

```

  

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Kotlin runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/kotlin-1.6/example).

  

4. Curl Command ( Email )

  

```bash

curl  -X  POST  http://localhost:3000/  -d  '{"variables": {"MAILGUN_API_KEY":"YOUR_MAILGUN_API_KEY","MAILGUN_DOMAIN":"YOUR_MAILGUN_DOMAIN"},"payload": "{\"type\": \"Email\",\"receiver\": \"hello@example.com\",\"message\": \"Programming is fun!\",\"subject\": \"Programming is funny!\"}"}'  -H  "X-Internal-Challenge: secret-key"  -H  "Content-Type: application/json"

```

  

## 📝 Notes

  

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
READ.me.md
Displaying READ.me.md.