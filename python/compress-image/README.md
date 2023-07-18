# 🖼️ Compress Image with TinyPNG and KrakenIO

A Python Cloud Function for compressing images without losing quality using [Tinypng API](https://tinypng.com/) and [KrakenIO](https://kraken.io/).


Example input with Tinypng:
```json
{
    "provider":"tinypng",
    "image":"iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="
}
```
Example input with KrakenIO:
```json
{
    "provider":"krakenio",
    "image":"iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="
}
```

Example output:
```json
{
    "success":true,
    "image":"iVBORw0KGgoAAAANSUhE...o6Ie+UAAAAASU5CYII="
}
```
Example error output:
```json
{
    "success":false,
    "image":"iVBORw0KGgoAAAANSUhE...o6Ie+UAAAAASU5CYII="
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **API_KEY** - Tinypng API Key or KrakenIO API Key
- **SECRET_API_KEY** - KrakenIO Secret API Key


ℹ️ _Create your TinyPNG API key at https://tinypng.com/developers_. <br>
ℹ️ _Create your KrakenIO API key at https://kraken.io/docs/getting-started_. <br>


## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```bash
git clone https://github.com/open-runtimes/examples.git
cd examples/python/compress-image
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
>TinyPNG Curl Example (Supports only API_KEY in Environment Variables)
```bash
curl http://localhost:3000/ -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json" -d '{"payload": {"provider": "tinypng", "image": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAA1JREFUGFdj+L+U4T8ABu8CpCYJ1DQAAAAASUVORK5CYII="}, "variables": {"API_KEY": "<YOUR_API_KEY>"}}'
```
>KrakenIO Curl Example (Supports API_KEY and SECRET_API_KEY in Environment Variables)
```bash
curl http://localhost:3000/ -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json" -d '{"payload": {"provider": "krakenio", "image": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAA1JREFUGFdj+L+U4T8ABu8CpCYJ1DQAAAAASUVORK5CYII="}, "variables": {"API_KEY": "<YOUR_API_KEY>", "SECRET_API_KEY": "<YOUR_SECRET_API_KEY>"}}'
```
## 📝 Notes
- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Python 3.10. Other versions may work but are not guaranteed to work as they haven't been tested.
