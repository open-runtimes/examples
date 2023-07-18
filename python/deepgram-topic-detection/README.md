# 🧹Deepgram Topic Detection

A Cloud Function for topic detection using [Deepgram](https://deepgram.com/)

_Example input:_

```json
{
  "fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"
}
```

_Example output:_

```json
{
  "deepgramData": {},
  "success": true
}
```

_Example error output:_

```json
{
  "success": false,
  "message": "Error"
}
```

## 📝 Environment Variables

**DEEPGRAM_API_KEY** - Your Deepgram secret API key.
Details are under link: [Deepgram_getting_started](https://developers.deepgram.com/documentation/getting-started/)

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```bash
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd python/deepgram-topic-detection
```

2. Build the code:

```bash
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=main.py --rm --interactive --tty --volume $PWD:/usr/code openruntimes/python:v2-3.10 sh /usr/local/src/build.sh
```

3. Spin-up open-runtime:

```bash
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/python:v2-3.10 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/python-3.10).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).