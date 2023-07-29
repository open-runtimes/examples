# 🧹 Deepgram Topic Detection

A Cloud Function for topic detection using [Deepgram](https://deepgram.com/)

_Example input:_

```json
{
  "fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"
}
```

_Example output:_

```json
{ "success": true, "deepgramData": {} }
```

_Example error output:_

```json
{ "success": false, "message": "Please provide a valid file URL." }
```

## 📝 Environment Variables

**DEEPGRAM_API_KEY** - Your Deepgram secret API key.
Details are under link: [Deepgram_getting_started](https://developers.deepgram.com/documentation/getting-started/)

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd kotlin/deepgram_topic_detection
```

2. Build the code:

```bash
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/Index.kt --rm --interactive --tty --volume $PWD:/usr/code openruntimes/kotlin:v2-1.6 sh /usr/local/src/build.sh
```

3. Spin-up open-runtime:

```bash
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/kotlin:v2-1.6 sh /usr/local/src/start.sh
```

4. Run the curl function to send request.

```bash
curl --location --request POST 'http://localhost:3000/' \
--header 'X-Internal-Challenge: secret-key' \
--header 'Content-Type: application/json' \
--data-raw '{"payload": "{\"fileUrl\": \"https://static.deepgram.com/examples/interview_speech-analytics.wav\"}",
"variables": {"DEEPGRAM_API_KEY":"<YOUR_API_KEY>"}}
'
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Kotlin runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/kotlin-1.6).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
