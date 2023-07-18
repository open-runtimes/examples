# 🔉📃 Transcribe Video with Deepgram

A Ruby Cloud Function for transcribing video using [Deepgram API](https://deepgram.com/).

_Example input:_

```json
{
    "fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"
}
```

_Example output:_


```json
{
    "success": "true",
    "deepgramData": "another big problem in the speech analytics space. When customers first bring the software on. Is that they they are blown away by the fact that an engine can monitor hundreds of Kpis. Right? Everything from my..."
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **DEEPGRAM_API_KEY** - Deepgram API Key

ℹ️ _Create your Deepgram API key at console.deepgram.com_

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
git clone https://github.com/open-runtimes/examples.git && cd examples
cd ruby/deepgram-transcribe-video
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/ruby:v2-3.1 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.rb --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/ruby:v2-3.1 sh /usr/local/src/start.sh
```



Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1).

> Make sure to replace `YOUR_API_KEY` without your key.
Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1).

4. Run the curl function to send request.
```
curl http://localhost:3000/ -d '{"payload": {"fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"}, "variables": {"DEEPGRAM_API_KEY": "<YOUR_API_KEY>"}}' -H "X-Internal-Challenge: secret-key"
```



## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Ruby 3.1. Other versions may work but are not guaranteed to work as they haven't been tested.
