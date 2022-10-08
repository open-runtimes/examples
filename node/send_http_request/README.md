# 📱 Send Http Request

A Node Cloud Function that figures out country in which a phone number is registered.

_Example input function payload:_

```json
{
  "url": "https://demo.appwrite.io/v1/locale/countries/eu",
  "method": "GET",
  "headers": { "x-client-version": "1.0.0" },
  "body": ""
}
```

_Successful function response:_

```json
{
    "success":true,
    "response":{"headers":{},"code":200,"body":"{\"total\":27,\"countries\":[]}"}
}
```

_Error function response:_

```json
{
    "success":false,
    "message":"URL could not be reached."
}

```

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```bash
git clone https://github.com/open-runtimes/examples.git && cd examples
cd node/send_http_request
```

2. Enter this function folder and build the code:
   
```bash
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:v2-16.0 sh /usr/local/src/build.sh
```

As a result, a `code.tar.gz` file will be generated.

1. Start the Open Runtime:

```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.js --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/node:v2-18.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Node runtime [README](https://github.com/open-runtimes/open-runtimes/tree/6d07f73a5984a0014a3bd297499ecb4dcc0e38f4/runtimes/node-18.0).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Node 16.0. Other versions may work but are not guaranteed to work as they haven't been tested.
