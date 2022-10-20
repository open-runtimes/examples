# Transcribe Audio Files

A Python Cloud Function that transcribes an Audio file with the help of Deepgram.

_Example input 1:_

```json
{
    "variables": {
        "DEEPGRAM_API_KEY": "<DEEPGRAM_API_KEY>"
    },
    "payload": "{\"fileUrl\": \"https://static.deepgram.com/examples/interview_speech-analytics.wav\"}"
}
```

_Example output 1:_

```json
{
    "response": {
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
				...
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
    "variables": {
        "DEEPGRAM_API_KEY": "<DEEPGRAM_API_KEY>"
    },
    "payload": "{\"fileUrl\": \"WRONG_URL\"}"
}
```

_Example output 2:_

```json
{
    "response": {
        "message": "Please provide a valid audio URL",
        "success": false
    }
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **APPWRITE_FUNCTION_API_KEY** - Deepgram API Key

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

    ```shell
    git clone https://github.com/open-runtimes/examples.git && cd examples
    cd python/convert_phone_number_to_country_name
    ```

2. Enter this function folder and build the code:

    ```shell
    docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/python:v2-3.10 sh /usr/local/src/build.sh
    ```

    As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

    ```shell
    docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=main.py --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/python:v2-3.10 sh /usr/local/src/start.sh
    ```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/python-3.10).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Python 3.10. Other versions may work but are not guaranteed to work as they haven't been tested.
