# 🗣️ Text To Speech with Google, Azure and AWS API

A Python cloud function for text to speech synthesis using [Google](https://cloud.google.com/text-to-speech), [Azure](https://azure.microsoft.com/en-us/products/ai-services/text-to-speech) and [AWS](https://docs.aws.amazon.com/polly/latest/dg/API_SynthesizeSpeech.html).

### Supported Providers and Language Codes
| Providers | Language Code (BCP-47) |
| ----------- | ----------- |
| Google      |[Google Language Code](https://cloud.google.com/text-to-speech/docs/voices) |
| Azure       |[Azure Language Code](https://learn.microsoft.com/en-us/azure/ai-services/speech-service/language-support?tabs=stt)  |
| AWS         |[AWS Language Code](https://docs.aws.amazon.com/polly/latest/dg/API_SynthesizeSpeech.html) |

### Example Input:
```json
{
    "provider":"<YOUR_PROVIDER_HERE>",
    "language":"<YOUR_LANGUAGE_CODE>",
    "text":"Hello world!"
}
```
### Example output:
```json
{
    "success":true,
    "audio_bytes":"iVBORw0KGgoAAAANSUhE...o6Ie+UAAAAASU5CYII="
}
```
### Example error output:
```json
{
    "success":false,
    "error":"Missing API_KEY""
}
```

## 📝 Environment Variables
List of environment variables used by this cloud function:
- **API_KEY** - Supported with Google, Azure, and AWS.
- **PROJECT_ID** - Supported with Google.
- **SECRET_API_KEY** - Supported with AWS.

| **Google**| **AWS** | **Azure** |
| --------  | --------  | -------- |
|API_KEY    | API_KEY   | API_KEY
|PROJECT_ID |SECRET_API_KEY| 


## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```bash
git clone https://github.com/open-runtimes/examples.git && cd examples
cd python/text-to-speech
```

2. Enter this function folder and build the code:
```bash
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/python:v2-3.10 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```bash
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=main.py --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/python:v2-3.10 sh /usr/local/src/start.sh
```

> Make sure to replace `YOUR_API_KEY` with your key.
Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/openruntimes/python:v2-3.10).

4. Run the cURL function to send request.
>Google Curl Example (Supports only API_KEY and PROJECT_ID in Environment Variables)
```bash
curl http://localhost:3000/ -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json" -d '{"payload": {"provider": "google", "language": "en-US", "text": "Hello World!"}, "variables": {"API_KEY": "<YOUR_API_KEY>", "PROJECT_ID": "<YOUR_PROJECT_ID>"}}'
```
>Azure Curl Example (Supports API_KEY in Environment Variables)
```bash
curl http://localhost:3000/ -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json" -d '{"payload": {"provider": "azure", "language":"en-US", "text": "Hello World!"}, "variables": {"API_KEY": "<YOUR_API_KEY>"}}'
```
>AWS Curl Example (Supports API_KEY and SECRET_API_KEY in Environment Variables)
```bash
curl http://localhost:3000/ -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json" -d '{"payload": {"provider": "aws", "language":"en-US", "text":"Hello World!"}, "variables": {"API_KEY": "<YOUR_API_KEY>", "SECRET_API_KEY": "<YOUR_SECRET_API_KEY>"}}'
```
## 📝 Notes
- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Python 3.10. Other versions may work but are not guaranteed to work as they haven't been tested.
