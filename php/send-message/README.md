# 📬 Send Message using a specific channel.

An PHP Cloud Function sending a message using a specific channel.

This example includes the following channels, `SMS`, `Email`, `Twitter`, and `Discord`.

*Example input: (SMS)*

```json
{
  "type": "SMS",
  "receiver": "+123456789",
  "message": "Programming is fun!"
}
```

*Example input: (Email)*

```json
{
  "type": "Email",
  "receiver": "user@example.app",
  "subject": "Welcome to the work of Cloud Functions",
  "message": "Email sent from a PHP Cloud Function. 👀"
}
```

*Example input: (Twitter)*

```json
{
  "type": "Twitter",
  "message": "Tweet sent from a PHP Cloud Function. 🐦"
}
```

*Example input: (Discord)*

```json
{
  "type": "Discord",
  "message": "Message sent from a PHP Cloud Function. 😏"
}
```

*Example output: (Success)*

```json
{
  "success": true
}
```

*Example output: (Failure)*

```json
{
  "success": false,
  "message": "Receiver is not a valid email."
}
```

## 📝 Variables

List of variables used by this cloud function:

#### Mailgun

- **MAILGUN_API_KEY** - API Key for Mailgun 
- **MAILGUN_DOMAIN** - Domain Name from Mailgun

#### Discord

- **DISCORD_WEBHOOK_URL** - Webhook URL for Discord 

#### Twilio

- **TWILIO_ACCOUNT_SID** - Acount SID from Twilio
- **TWILIO_AUTH_TOKEN** - Auth Token from Twilio
- **TWILIO_SENDER** - Sender Phone Number from Twilio

#### Twitter

- **TWITTER_API_KEY** - API Key for Twitter
- **TWITTER_API_KEY_SECRET** - API Key Secret for Twitter
- **TWITTER_ACCESS_TOKEN** - Access Token from Twitter
- **TWITTER_ACCESS_KEY_SECRET** - Access Token Secret from Twitter

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd php/send_message
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/php:v2-8.1 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.php --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/php:v2-8.1 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit PHP runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/php-8.1).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with PHP 8.1. Other versions may work but are not guaranteed to work as they haven't been tested.
