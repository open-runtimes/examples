# 🔉📃 Summarize Audio with Deepgram

A Kotlin Cloud Function for summarizing audio number using [Deepgram API](https://deepgram.com/).

_Example input:_

```json
{
  "fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"
}
```

_Example output:_

```json
{
  "success": true,

  "deepgramData": [
    {
      "summary": "another big problem in the speech analytics space. When customers first bring the software on is that they they are blown away by the fact that an engine can monitor hundreds of Kpis.",

      "start_word": 0,

      "end_word": 228
    }
  ]
}
```

_Example error output:_

```json
{
  "success": false,

  "message": "Error Message"
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **DEEPGRAM_API_KEY** - Deepgram API Key

ℹ️ _Create your Deepgram API key at console.deepgram.com_

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```bash

git clone  https://github.com/open-runtimes/examples.git && cd  examples

cd  kotlin/deepgram-audio-summary

```

2. Build the code:

```bash

docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/Index.kt --rm --interactive --tty --volume ${PWD}:/usr/code openruntimes/kotlin:v2-1.6 sh /usr/local/src/build.sh
```

3. Spin-up open-runtime:

```bash

docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume ${PWD}/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/kotlin:v2-1.6 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Kotlin runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/kotlin-1.6).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
