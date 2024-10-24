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

There are two ways of deploying the Appwrite function, both having the same results, but each using a different process. 

### Using CLI

Make sure you have [Appwrite CLI](https://appwrite.io/docs/command-line#installation) installed, and you have successfully logged into your Appwrite server. To make sure Appwrite CLI is ready, you can use the command `appwrite client --debug` and it should respond with green text `✓ Success`.

Make sure you are in the same folder as your `appwrite.json` file and run `appwrite deploy function` to deploy your function. You will be prompted to select which functions you want to deploy.

### Manual Deployment 

Manual deployment has no requirements and uses Appwrite Console to deploy the tag. First, enter the folder of your function. Then, create a tarball of the whole folder and gzip it using the following command. In order to create a tar ball. Enter this command to build the code.
```
docker run --rm --interactive --tty \
  -e INTERNAL_RUNTIME_ENTRYPOINT=Index.cs \
  --volume $PWD:/usr/code \
  openruntimes/dotnet:v2-6.0 sh \
  /usr/local/src/build.sh
```  
Then
```
tar -czf code.tar.gz --exclude code.tar.gz .
```

After creating `.tar.gz` file, visit Appwrite Console, click on the `Create deployment` button and switch to the `Manual` tab. There, set the `entrypoint` to `Index.cs`, and upload the file we just generated.

### Executing the function without the Appwrite Console

After you have built the code with the command above, run the following up command to start a server to listen to your request on your local host.
```
docker run --rm --interactive --tty \
  -p 3000:3000 \
  -e INTERNAL_RUNTIME_KEY=secret-key \
  -e INTERNAL_RUNTIME_ENTRYPOINT=Index.cs \
  --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro \
  openruntimes/dotnet:v2-6.0 \
  sh /usr/local/src/start.sh
```
Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers.

Sample execution using curl
```
curl -X POST http://localhost:3000/ -d '{"variables": {"DISCORD_WEBHOOK_URL":"YOUR_DISCORD_WEBHOOK_URL"},"payload": "{\"type\": \"Discord\",\"message\": \"Programming is fun!\",}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with C# and .NET.
