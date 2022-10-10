# 📸 Generate Website Screenshot

A Node Cloud Function for generating website screenshot.

_Example Valid input:_

```json
{
    "url": "https://google.com/"
}
```

_Successful Response:_


```json
{
    "success": true,
    "screenshot": "iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="
}
```
_Example Invalid input:_

```json
{
    "url": "https://googlegoogle.com/"
}
```

_Error Response:_


```json
{
    "success": false,
    "message": "Website could not be reached."
}
```

## 📝 Environment Variables

No environment variables needed.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd node/generate_website_screenshot
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:17.0 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/index.js -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/node:17.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit NodeJS runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/node-17.0).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with NodeJS 17.0. Other versions may work but are not guaranteed to work as they haven't been tested.