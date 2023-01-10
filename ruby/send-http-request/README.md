# 🌐 Send a HTTP Request

A Ruby Cloud Function for sending HTTP requests that supports custom headers and body.

_Example input:_

```json
{
    "headers": {},
    "variables": {},
    "payload": "{\"url\":\"https:\/\/demo.appwrite.io\/v1\/locale\/countries\/eu\",\"method\":\"GET\",\"headers\":{\"x-client-version\":\"1.0.0\"},\"body\":\"\"}"
}
```

_Example output:_

```json
{
    "success": true,
    "response": {
        "headers": {
            "access-control-allow-credentials": [
              "true"
            ],
            "access-control-allow-headers": [
              "Origin, Cookie, Set-Cookie, X-Requested-With, Content-Type, Access-Control-Allow-Origin, Access-Control-Request-Headers, Accept, X-Appwrite-Project, X-Appwrite-Key, X-Appwrite-Locale, X-Appwrite-Mode, X-Appwrite-JWT, X-Appwrite-Response-Format, X-SDK-Version, X-SDK-Name, X-SDK-Language, X-SDK-Platform, X-Appwrite-ID, Content-Range, Range, Cache-Control, Expires, Pragma"
            ],
            "access-control-allow-methods": [
              "GET, POST, PUT, PATCH, DELETE"
            ],
            "access-control-allow-origin": [
              "https://localhost"
            ],
            "access-control-expose-headers": [
              "X-Fallback-Cookies"
            ],
            "content-length": [
              "302"
            ],
            "content-type": [
              "application/json; charset=UTF-8"
            ],
            "date": [
              "Tue, 04 Oct 2022 08:02:27 GMT"
            ],
            "server": [
              "Appwrite"
            ],
            "strict-transport-security": [
              "max-age=10886400"
            ],
            "x-content-type-options": [
              "nosniff"
            ],
            "x-debug-fallback": [
              "true"
            ],
            "x-debug-speed": [
              "0.0030789375305176"
            ],
            "connection": [
              "close"
            ]
        },
        "code": 200,
        "body": "{\"total\":27,\"countries\":[{\"name\":\"Austria\",\"code\":\"AT\"},{\"name\":\"Belgium\",\"code\":\"BE\"},{\"name\":\"Bulgaria\",\"code\":\"BG\"},{\"name\":\"Croatia\",\"code\":\"HR\"},{\"name\":\"Cyprus\",\"code\":\"CY\"},{\"name\":\"Czechia\",\"code\":\"CZ\"},{\"name\":\"Denmark\",\"code\":\"DK\"},{\"name\":\"Estonia\",\"code\":\"EE\"},{\"name\":\"Finland\",\"code\":\"FI\"},{\"name\":\"France\",\"code\":\"FR\"},{\"name\":\"Germany\",\"code\":\"DE\"},{\"name\":\"Greece\",\"code\":\"GR\"},{\"name\":\"Hungary\",\"code\":\"HU\"},{\"name\":\"Ireland\",\"code\":\"IE\"},{\"name\":\"Italy\",\"code\":\"IT\"},{\"name\":\"Latvia\",\"code\":\"LV\"},{\"name\":\"Lithuania\",\"code\":\"LT\"},{\"name\":\"Luxembourg\",\"code\":\"LU\"},{\"name\":\"Malta\",\"code\":\"MT\"},{\"name\":\"Netherlands\",\"code\":\"NL\"},{\"name\":\"Poland\",\"code\":\"PL\"},{\"name\":\"Portugal\",\"code\":\"PT\"},{\"name\":\"Romania\",\"code\":\"RO\"},{\"name\":\"Slovakia\",\"code\":\"SK\"},{\"name\":\"Slovenia\",\"code\":\"SI\"},{\"name\":\"Spain\",\"code\":\"ES\"},{\"name\":\"Sweden\",\"code\":\"SE\"}]}"
 }
}
```

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```bash
git clone https://github.com/open-runtimes/examples.git && cd examples
cd ruby/send-http-request
```

2. Enter this function folder and build the code:

```bash
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/ruby:3.1 sh /usr/local/src/build.sh
```

As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

```bash
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.rb --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/ruby:3.1 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit the Ruby runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Ruby 3.1. Other versions may work but are not guaranteed to work as they haven't been tested.
