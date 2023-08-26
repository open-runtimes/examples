# Transcribe Audio Files

A Swift Cloud Function that transcribes an Audio file with the help of Deepgram.

_Example input 1:_

```json
{
    "fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"
}
```

_Example output 1:_

```json
{
	"deepgramData": {
	    "confidence": 0.9926758,
	    "transcript": "another big problem in the ... company succeed using this product",
	    "words": [
		{
		    "confidence": 0.9995117,
		    "end": 0.839599,
		    "start": 0.33959904,
		    "word": "another"
		},
		{
		    "confidence": 0.99365234,
		    "end": 95.770004,
		    "start": 95.41,
		    "word": "product"
		}
	    ]
	},
        "success": true
}
```

_Example input 2:_

```json
{

    "fileUrl": "WRONG_URL"
}
```

_Example output 2:_

```json
{
	"message": "Please provide a valid audio URL",
	"success": false
}
```

## 📝 Variables

List of variables used by this cloud function:

- **DEEPGRAM_API_KEY** - Deepgram API Key

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

    ```shell
    git clone https://github.com/open-runtimes/examples.git && cd examples
    cd swift/deepgram-transcribe-audio
    ```

2. Enter this function folder and build the code:

    ```shell
    docker run --rm --interactive --tty --volume $PWD:/mnt/code openruntimes/swift:v3-5.5 sh helpers/build.sh
	```

    As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

    ```shell
   docker run -p 3000:3000 -e OPEN_RUNTIMES_SECRET=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/mnt/code/code.tar.gz:ro openruntimes/swift:v3-5.5 sh helpers/start.sh "/usr/local/server/src/function/Runtime serve --env production --hostname 0.0.0.0 --port 3000"
    ```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Swift runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/swift-5.5).

4. Run the cURL function to send request.
```bash
curl -g -X POST http://localhost:3000/ -H "x-open-runtimes-secret: secret-key" -H "Content-type: application/json" -d '{"payload":"{\"fileUrl\":\"https://static.deepgram.com/examples/interview_speech-analytics.wav\"}","variables":{"DEEPGRAM_API_KEY":"<YOUR_API_KEY>"}}'
```

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
