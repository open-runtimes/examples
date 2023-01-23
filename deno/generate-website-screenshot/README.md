# 🖼️ Get Website ScreenShot from url

A Deno Cloud Function for generating screenshot of a website from the [screenshotapi.net](https://screenshot-api.gitbook.io/screenshot-api-docs/).

_Example input:_

```json
{
    "url":"https://appwrite.io/"
}

```

_Example output:_


```json
{
    "sucess": true,
    "screenshot": "iVBORw0KGgoAAAANSUh...XWkulRXfkcAAAAASUVORK5CYII="
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

**SCREEN_SHOT_API_KEY** - Your screenshotapi.net API key.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/generate_website_screenshot
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts --rm --interactive --tty --volume $PWD:/usr/code openruntimes/deno:v2-1.24 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/deno:v2-1.24 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Deno runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/deno-1.24).

4. Execute function:

```
curl http://localhost:3000/ -d '{"variables":{"SCREEN_SHOT_API_KEY":"25PVQ6R-R124R86-PKB2H0A-Z61P1JR"},"payload":"{\"url\":\"https://appwrite.io/\"}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

## 📝 Notes
- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Deno 1.13 and 1.14. Other versions may work but are not guarenteed to work as they haven't been tested.