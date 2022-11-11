# SendMessage()

A Python Cloud Function for sending a message using a specific channel to a receiver

Supported channels are `SMS`, `EMAIL` ,`DISCORD` and `TWITTER`.

_SMS Example payload_

```json
{ "type": "SMS", "receiver": "+123456789", "message": "Programming is fun!" }
```

_Email Example payload_

```json
{
  "type": "EMAIL",
  "receiver": "hello@example.com",
  "message": "Programming is fun!",
  "subject": "Programming is funny!"
}
```

_Discord Example payload_

```json
{ "type": "DISCORD", "message": "Hi" }
```

_Twitter Example payload_

```json
{ "type": "TWITTER", "receiver": "", "message": "Programming is fun!" }
```

_Successful function response:_

```json
{ "success": true }
```

_Error function response:_

```json
{
  "success": false,
  "error": "Failed to send message,check webhook URL"
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

Mailgun

- **MAILGUN_API_KEY** - API Key for Mailgun
- **MAILGUN_DOMAIN** - Domain Name from Mailgun

Discord

- **DISCORD_WEBHOOK_URL** - Webhook URL for Discord

Twilio

- **TWILIO_ACCOUNT_SID** - Acount SID from Twilio
- **TWILIO_AUTH_TOKEN** - Auth Token from Twilio
- **TWILIO_SENDER** - Sender Phone Number from Twilio
  Twitter
- **TWITTER_API_KEY** - API Key for Twitter
- **TWITTER_API_KEY_SECRET** - API Key Secret for Twitter
- **TWITTER_ACCESS_TOKEN** - Access Token from Twitter
- **TWITTER_ACCESS_TOKEN_SECRET** - Access Token Secret from Twitter

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd python/send_message
```

2. Enter this function folder and build the code:

```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/python:v2-3.10 sh /usr/local/src/build.sh
```

As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=main.py --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/python:v2-3.10 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/python-3.10/example).

4. Curl Command

```bash
curl http://localhost:3000/ -d '{"variables": {"Add":"Required Variables"},"payload": {"type": "EMAIL","receiver": "hello@example.com","message": "Programming is fun!","subject": "Programming is funny!"}}' -H "X-Internal-Challenge: secret-key"
```

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
