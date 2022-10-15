# SendMessage() 

A sample Deno Cloud Function sending a message using a specific channel to a receiver

Supported channels are `SMS`, `EMAIL` ,`DISCORD` and `TWITTER`.


_Example function payloads:_

```json
// Sending SMS
{
    "type":"SMS","receiver":"+123456789","message":"Programming is fun!"
}
// Sending Email
{
    "type":"EMAIL","receiver":"hello@example.com",
    "message":"Programming is fun!","subject":"Programming is funny!"
}
// Sending message through Discord webhook 
{
    "type":"DISCORD","receiver":"","message":"Hi"
}
// Sending a tweet
{
    "type":"TWITTER","receiver":"","message":"Programming is fun!"
}
```


_Successful function response:_


```json
{ "success" : true }
```

_Error function response:_


```json
{
	"success": false,
	"error": "Failed to send message, check the webhook URL"
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

Mailgun
* **MAILGUN_API_KEY** - API Key for Mailgun 
* **MAILGUN_DOMAIN** - Domain Name from Mailgun

Discord
* **DISCORD_WEBHOOK_URL** - Webhook URL for Discord 

Twilio 

* **TWILIO_ACCOUNT_SID** - Acount SID from Twilio

* **TWILIO_AUTH_TOKEN** - Auth Token from Twilio

* **TWILIO_SENDER** - Sender Phone Number from Twilio

Twitter

* **TWITTER_API_KEY** - API Key for Twitter

* **TWITTER_API_KEY_SECRET** - API Key Secret for Twitter

* **TWITTER_ACCESS_TOKEN** - Access Token from Twitter

* **TWITTER_ACCESS_TOKEN_SECRET** - Access Token Secret from Twitter

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/send_message
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts --rm --interactive --tty --volume $PWD:/usr/code openruntimes/deno:v2-1.21 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/deno:v2-1.21 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Deno runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/deno-1.14).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Deno 1.13 and 1.14. Other versions may work but are not guarenteed to work as they haven't been tested.
