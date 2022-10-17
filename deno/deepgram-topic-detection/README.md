# ⚡ deepgramLanguageDetection()

A Deno Cloud Function for detecting language from audio using [Deepgram](https://deepgram.com/)

_Example function payload:_

```json
{
    {
        "fileUrl":"https://static.deepgram.com/examples/interview_speech-analytics.wav"
    }
}
```

_Successful function response::_

```json
{ "deepgramData": {}, "success": true }
```

_Error function response:_

```json
{ "message": "error message", "success": false }
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

**DEEPGRAM_API_KEY** - Your Deepgram API key.## 🚀 Deployment

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/send_message
```

2. Enter this function folder and build the code:

```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts --rm --interactive --tty --volume $PWD:/usr/code openruntimes/deno:v2-1.21 sh /usr/local/src/build.sh
```

As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/deno:v2-1.21 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Deno runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/deno-1.14).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Deno 1.13 and 1.14. Other versions may work but are not guarenteed to work as they haven't been tested.
