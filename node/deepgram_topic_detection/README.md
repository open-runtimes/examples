# 🕵️ Deepgram Topic Detection

A Node Cloud Function that detect list of topic from spoken audio using [Deepgram API](https://developers.deepgram.com/documentation/).

_Example input function payload:_

```json
{ "fileUrl": "url_of_the_wav_file" }
```

_Successful function response:_

```json
{
  "success": true,
  "deepgramData": {}
}
```

_Error function response:_

```json
{
  "success": false,
  "message": "Please provide a valid file URL and API key."
}
```

## 📝 Variables

List of variables used by this cloud function:

- **DEEPGRAM_API_KEY** - Your Deepgram API key.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd node/deepgram_topic_detection
```

2. Enter this function folder and build the code:

```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:v2-18.0 sh /usr/local/src/build.sh
```

As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=src/index.js --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/node:v2-18.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request to `http://localhost:3000` with appropriate authorization headers. To learn more about runtime, you can visit NodeJS runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/node-18.0).

## 🧪 Test Function Execution

In new terminal window, execute function to send request:

```bash
curl -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json" -X POST http://localhost:3000/ -d '{"payload": "{\"fileUrl\":\"url_of_the_wav_file\"}", "variables": {"DEEPGRAM_API_KEY": "your_deepgram_api_key"} }'
```

- Make sure to use bash terminal, if you are using other terminal then modify the `payload` accordingly
- Replace the `url_of_the_wav_file` and `your_deepgram_api_key`
- Make sure you have header `x-internal-challenge: secret-key`.

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with NodeJS 18.0. Other versions may work but are not guarenteed to work as they haven't been tested.
