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
$ cd dotnet/sendMessage
```

2. Enter this function folder and build the code:

```
docker run -e OPEN_RUNTIMES_ENTRYPOINT=Index.cs --rm --interactive --tty --volume $PWD:/mnt/code openruntimes/dotnet:v3-7.0 sh helpers/build.sh

```

3. Start the Open Runtime:

```
docker run -p 3000:3000 -e OPEN_RUNTIMES_SECRET=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/mnt/code/code.tar.gz:ro openruntimes/dotnet:v3-7.0 sh helpers/start.sh "dotnet /usr/local/server/src/function/DotNetRuntime.dll"
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit .NET Runtime 7.0 [README](https://github.com/open-runtimes/open-runtimes/blob/main/runtimes/dotnet-7.0/README.md).

4. Sample curl command (Email)
```
curl -H "x-open-runtimes-secret: secret-key" -H "Content-Type: application/json" -X POST http://localhost:3000/ -d '{"variables":{"MAILGUN_DOMAIN":"sandbox4b061e2c81e94adda674598971b62172.mailgun.org", "MAILGUN_API_KEY":"1867c7be2b0c41bcb8b1bb1f8500fd6e-28e9457d-57f47498"},"payload":{"type":"Email","message":"I am testing my cloud function","receiver":"oluwafemisire.ojuawo@kibo.school", "subject":"Testing"}}'
```


## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with C# and .NET.
