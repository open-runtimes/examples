# 📝 Transcribe audio using Deepgram

A Dart Cloud Function that transcribes pre-recorded audio using [Deepgram](https://developers.deepgram.com/).

_Example input 1:_

```json
{
    "payload": "{\"fileUrl\": \"https://static.deepgram.com/examples/Bueller-Life-moves-pretty-fast.wav\"}"
}
```

_Example output 1:_

```json
{
    "success": true,
    "deepgramData": { "A big": "Object" }
}
```

_Example input 2:_
```json
{
    "payload": "{\"fileUrl\": \"Wrong\"}"
}
```

_Example output 2:_
```json
{
    "success": false,
    "message": "Failed to parse URL."
}
```

_Example input 3:_

```json
{
     "payload": "{}"
}
```

_Example output 3:_

```json
{
    "success": false,
    "message": "Please provide a file url in payload"
}
```

If the API Key is not provided, you would get:

```json
{
    "success": false,
    "message": "Please provide the api key in environment variables"
}
```

## 📝 Variables

List of variables used by this cloud function:

* **DEEPGRAM_API_KEY** - API Key for Deepgram

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd dart/deepgram-Transcribe-Audio
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=lib/main.dart --rm --interactive --tty --volume ${PWD}:/usr/code openruntimes/dart:v2-2.16 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume ${PWD}/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/dart:v2-2.16 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Dart runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/dart-2.16).

## 📝 Notes
- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Dart 2.18. Other versions may work but are not guaranteed to work as they haven't been tested. Versions below Dart 2.14 will not work, because Apwrite SDK requires Dart 2.14,
