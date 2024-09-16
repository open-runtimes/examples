# sendMessage()

A Swift Cloud Function for sending a message using a specific channel to a receiver

Supported channels are `SMS`, `Email` ,`Discord` and `Twitter`.

_SMS Example payload_

```json
{ 
  "type": "SMS", 
  "recipient": "+123456789", 
  "content": "Programming is fun!" 
}
```

_Email Example payload_

```json
{
  "type": "Email",
  "recipient": "hello@example.com",
  "content": "Programming is fun!",
  "subject": "Programming is funny!"
}
```

_Discord Example payload_

```json
{
  "type": "Discord",
  "content": "Hi",
  "recipient": "username"
}
```

_Twitter Example payload_

```json
{
  "type": "Twitter",
  "recipient": "",
  "content": "Programming is fun!"
}
```
- User specified in receiver will be tagged at the beginning of the tweet as a 'reply'. If no user specified, tweet will be a standard tweet.


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
  "message": "Misconfigurtion Error: Missing environment variables."
}
```

## 📝 Variables

List of variables used by this cloud function:

Mailgun

- **MAILGUN_API_KEY** - API Key for Mailgun
- **MAILGUN_DOMAIN** - Domain Name from Mailgun
- **MAILGUN_FROM_EMAIL_ADDRESS** - Email value for sender

- Sender email address defaults to "me@samples.mailgun.org" if left empty. Format value as "Name <name@domain.com>" to display a name and email or "name@domain.com" for just the email.

Discord

- **DISCORD_BOT_TOKEN** - API token for Discord bot
- **DISCORD_GUILD_ID** - Discord server ID

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

```bash
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd swift/send-message
```

2. Build the code:

```bash
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/swift:v2-5.5 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.  

3. Start the Open Runtime:

```bash
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/swift:v2-5.5 sh /usr/local/src/start.sh
```
Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Swift runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/swift-5.5).

4. Curl Command ( Email )

```bash
curl -X POST http://localhost:3000/ -d '{"variables": {"MAILGUN_API_KEY":"YOUR_MAILGUN_API_KEY","MAILGUN_DOMAIN":"YOUR_MAILGUN_DOMAIN"},"payload": "{\"type\": \"email\",\"recipient\": \"hello@example.com\",\"content\": \"Programming is fun!\",\"subject\": \"Programming is funny!\"}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
