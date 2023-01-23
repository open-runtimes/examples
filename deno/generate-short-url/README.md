# 🌐 Shorten URL

A Deno Cloud Function for shortening URL from the [Bitly API](https://dev.bitly.com/) and [TinyURL API](https://tinyurl.com/app/dev).

_Example input:_

```json
{
  "provider": "bitly",
  "url": "https://google.com/"
}
```

```json
{
  "provider": "tinyurl",
  "url": "https://google.com/"
}
```

_Example output:_

```json
{
  "success": true,
  "url": "https://bit.ly/3y3HDkC"
}
```

```json
{
  "success": false,
  "message": "provider is required"
}
```

## 📝 Variables

List of variables used by this cloud function:

**BITLY_TOKEN** - Your Bitly API token.
**TINYURL_TOKEN** - Your TInyURL API token.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/generate_short_url
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
curl http://localhost:3000/ -d '{"variables":{"BITLY_TOKEN":"[YOUR_API_KEY]"},"payload": "{\"url\":\"https://appwrite.io/\",\"provider\":\"bitly\"}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Deno 1.21 and 1.24. Other versions may work but are not guarenteed to work as they haven't been tested.
