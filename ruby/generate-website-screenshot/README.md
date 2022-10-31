# 🌐 generateWebsiteScreenshot() Function using Ruby

Generate a PNG screenshot of a website and return in base64 format.

_Example input:_

```json
{
    "variables": {"SCREENSHOT_API_KEY": "SCREENSHOT_API_KEY"},
    "payload": "{\"url\":\"https://google.com\"}"
}
```

_Example output:_

```json
{
  "success": true,
  "screenshot": "iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="
}
```

- **SCREENSHOT_API_KEY** - Here is an api key you get from https://app.screenshotapi.net/

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd ruby/generate-website-screenshot
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

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Ruby runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Ruby v2-3.1. Other versions may work but are not guaranteed to work as they haven't been tested.
