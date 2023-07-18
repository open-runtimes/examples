# 📬 Send Message using a specific channel

A .NET Cloud Function for sending a message using a specific channel to a receiver.

Supported channels include, `SMS`, `Email` ,`Disocrd` and `Twitter`.

## Examples

### Example input: (Discord)

```markdown
{
    type = "Discord",
    message = "Message sent from a C# Cloud Function. 😏"
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
    subject = "Welcome to the work of Cloud Functions",
    message = "Email sent from a C# Cloud Function. 👀"
};
```

### Example input: (Twitter)

```markdown
{
    type = "Twitter",
    message = "Tweet sent from a C# Cloud Function. 🐦"
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

- **TWILIO_ACCOUNT_SID**
- **TWILIO_AUTH_TOKEN**
- **TWILIO_SENDER**

### Discord

- **DISCORD_WEBHOOK_URL**

### Mailgun

- **MAILGUN_API_KEY**
- **MAILGUN_DOMAIN**

### Twitter

- **TWITTER_API_KEY**
- **TWITTER_API_KEY_SECRET**
- **TWITTER_ACCESS_TOKEN**
- **TWITTER_ACCESS_KEY_SECRET**

<!-- To be continued  -->
<!-- To do:

1. Complete the variables
2. add the deployment step by step process
3. add the notes -->
