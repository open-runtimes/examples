# 🌐 Send HTTP request

A Python Cloud Function for sending an HTTP request. Supported options are method, url, body, and headers. Status code, response body, and response headers are returned.

_Example input:_

```json
{
    "url":"https://demo.appwrite.io/v1/locale/countries/eu",
    "method":"GET",
    "headers":
    {
        "x-client-version":"1.0.0"
    },
    "body":""
}
```

_Example success output:_


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

_Example failure output:_


```json
{
    "success":false,
    "message":"URL could not be reached"
}
```

## 📝 Environment Variables

No environment variables are required to run this function.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd python/send-http-request
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/python:v2-3.10 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=main.py --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/python:v2-3.10 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/python-3.10).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Python 3.10. Other versions may work but are not guaranteed to work as they haven't been tested.