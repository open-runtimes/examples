# 📱 Send SMS with Twilio

Welcome to the documentation of this function 👋 We strongly recommend keeping this file in sync with your function's logic to make sure anyone can easily understand your function in the future. If you don't need documentation, you can remove this file.

## 🤖 Documentation

A sample Ruby Cloud function to send an SMS message to a phone number.

_Example input:_

```json
{
    "payload": "{
        \"message\": \"The message body.\",
        \"receiver\": \"+15555555555\"
    }",
    "env": {
        "TWILIO_ACCOUNT_SID": "ACXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
        "TWILIO_AUTH_TOKEN": "your_auth_token",
        "TWILIO_SENDER": "+15555555555"
    }
}
```

_Example output:_

```json
{
    "messageId": "SMXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
}
```

## 📝 Environment Variables

- **TWILIO_ACCOUNT_SID** - Twilio account SID
- **TWILIO_AUTH_TOKEN** - Twilio auth token
- **TWILIO_SENDER** - Your Twilio phone number to send SMS from

ℹ️ _Find your Account SID and Auth Token at twilio.com/console._

## 🚀 Deployment

Copy this function folder somewhere and follow the instructions in the [Ruby runtime readme](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1#readme) to deploy it.

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it [here](https://github.com/open-runtimes/open-runtimes).
 - This example is compatible with Ruby 3.0 and 3.1
