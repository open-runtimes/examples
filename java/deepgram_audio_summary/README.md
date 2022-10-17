# 💻 Deepgram Audio Summary

A Java Cloud Function for generating a summary using [Deepgram](https://deepgram.com/) 

_Deepgram Example input:_

```json
{"fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"}
```
_Deepgram Example output:_

```json
{
    "success": true,
    "deepgramData": {}
}
```

_Error Example output:_

```json
{
    "success": false,
    "message":"Please provide a valid file URL."
}
```


## 📝 Environment Variables

List of environment variables used by this cloud function. These must be passed within 'variables' section of the request:

**DEEPGRAM_SECRET_KEY** - This API key would be the access token used by Deepgram for generating the Summary.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd java/deepgram_audio_summary
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=Index.java --rm --interactive --tty --volume $PWD:/usr/code openruntimes/java:v2-11.0 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/java:v2-11.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Java runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/java-11.0).

## 📝 Notes
- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Java 11.0. Other versions may work but are not guarenteed to work as they haven't been tested.