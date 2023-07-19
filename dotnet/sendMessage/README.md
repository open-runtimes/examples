# 📬 Send Message using a specific channel

A .NET Cloud Function for sending a message using a specific channel to a receiver.

Supported channels include, `SMS`, `Email` ,`Disocrd` and `Twitter`.

## Examples

### Example input: (Discord)

```markdown
{
    type = "Discord",
    message = "Programming is fun!"
};
```

### Example input: (SMS)

```markdown
{
    type = "SMS",
    receiver = "+123456789",
    message = "Programming is fun!"
};
```

### Example input: (Email)

```markdown
{
    type = "Email",
    receiver = "user@example.app",
    subject = "Programming is fun!",
    message = "Programming is fun!"
};
```

### Example input: (Twitter)

```markdown
{
    type = "Twitter",
    message = "Programming is fun!"
};
```

### Example output: (Success)

```markdown
{
  "success": true
}
```

### Example output: (Failure)

```markdown
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
- ***To be updated soon***

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with C# and .NET.
