# 📱 Send SMS with Twilio

A Ruby Cloud Function for sending messages to a phone number using [Twilio API](https://www.twilio.com/docs/usage/api).

_Example input:_

```json
{
    "message": "Hello from Open Runtimes 👋",
    "receiver": "+421XXXXXXXXX"
}
```

_Example output:_


```json
{
    "messageId": "SMXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **TWILIO_ACCOUNT_SID** - Twilio account SID
- **TWILIO_AUTH_TOKEN** - Twilio auth token
- **TWILIO_SENDER** - Sender's phone number from Twilio

ℹ️ _Find your Account SID and Auth Token at twilio.com/console._

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd ruby/send-sms-with-twilio
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/ruby:3.1 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.rb --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/ruby:3.1 sh /usr/local/src/start.sh
```

> Make sure to replace `secret-key` with your key.

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Ruby runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Ruby 3.1. Other versions may work but are not guaranteed to work as they haven't been tested.
