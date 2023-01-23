# 🔉📃 Summarize Audio with Deepgram

A NodeJS Cloud Function for summarizing audio number using [Deepgram API](https://deepgram.com/).

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

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **DEEPGRAM_API_KEY** - Deepgram API Key

ℹ️ _Create your Deepgram API key at console.deepgram.com_

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```bash
git clone https://github.com/open-runtimes/examples.git && cd examples
cd node/deepgram_audio_summary
```

2. Enter this function folder and build the code:

```bash
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:v2-18.0 sh /usr/local/src/build.sh
```

As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

```bash
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/index.js -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/node:v2-18.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit NodeJS runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/node-18.0). 

4. Run the cURL function to send request.

```bash
curl --location --request POST 'http://localhost:3000' --header 'Content-Type: application/json' --header 'X-Internal-Challenge: secret-key' --data-raw '{"variables": {"DEEPGRAM_API_KEY": "YOUR_DEEPGRAM_API_KEY"},"payload": {"fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"}}'
```

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with NodeJS 18.0. Other versions may work but are not guarenteed to work as they haven't been tested.
