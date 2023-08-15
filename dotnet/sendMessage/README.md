# 📬 Send Message using a specific channel

A .NET Cloud Function for sending a message using a specific channel to a receiver.

Supported channels include, `SMS`, `Email` ,`Disocrd` and `Twitter`.

## Examples

### Example input: (Discord)

```json
{
    "type": "Discord",
    "message": "Programming is fun!"
}
```

### Example input: (SMS)

```json
{
    "type": "SMS",
    "receiver": "+123456789",
    "message": "Programming is fun!"
}
```

### Example input: (Email)

```json
{
    "type": "Email",
    "receiver": "user@example.app",
    "subject": "Programming is fun!",
    "message": "Programming is fun!"
}
```

### Example input: (Twitter)

```json
{
    "type": "Twitter",
    "message": "Programming is fun!"
}
```

### Example output: (Success)

```json
{
  "success": true
}
```

### Example output: (Failure)

```json
{
  "success": false,
  "message": "Receiver is not a valid email."
}
```

## 📝 Variables

List of variables used by this cloud function:

### Twilio

- **TWILIO_ACCOUNT_SID** - Acount SID from Twilio
- **TWILIO_AUTH_TOKEN** - Auth Token from Twilio
- **TWILIO_SENDER** - Sender Phone Number from Twilio

### Discord

- **DISCORD_WEBHOOK_URL** - Webhook URL for Discord

### Mailgun

- **MAILGUN_API_KEY** - API Key for Mailgun
- **MAILGUN_DOMAIN** - Domain Name from Mailgun

### Twitter

- **TWITTER_API_KEY** - API Key for Twitter
- **TWITTER_API_KEY_SECRET** - API Key Secret for Twitter
- **TWITTER_ACCESS_TOKEN** - Access Token from Twitter
- **TWITTER_ACCESS_KEY_SECRET** - Access Token Secret from Twitter

## 🚀 Deployment

<!-- To do:
1. Update the deployment section  -->

1. Clone this repository, and enter this function folder:

```markdown
git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd dotnet/send_message
```

2. Enter this function folder and build the code:

```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/dotnet:v3-7.0 sh /usr/local/src/build.sh
```

3. Start the Open Runtime:

```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=Index.cs --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/dotnet:v3-7.0  sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit .NET Runtime 7.0 [README](https://github.com/open-runtimes/open-runtimes/blob/main/runtimes/dotnet-7.0/README.md).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with C# and .NET.
