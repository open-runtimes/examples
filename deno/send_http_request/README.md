# 🌐 Send HTTP request

A Deno Cloud Function for sending HTTP requests

_Example input:_

```json
{
    "url":"https://demo.appwrite.io/v1/locale/countries/eu","method":"GET",
    "headers":
    {
        "x-client-version":"1.0.0"
    },
    "body":"{}"
}
```

_Example output:_

```json
{
    "success":true,
    "response":
    {
        "headers":{},
        "code":200,
        "body":"{\"total\":27,\"countries\":[]}"
    }
}
```

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/send_http_request
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

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Deno runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/deno-1.14).

4. Execute function:

```shell
curl http://localhost:3000/ -d '{"payload": "{\"url\":\"https://demo.appwrite.io/v1/locale/countries/eu\",\"method\":\"GET\",\"headers\":{\"x-client-version\":\"1.0.0\"}}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Deno 1.24. Other versions may work but are not guarenteed to work as they haven't been tested.
