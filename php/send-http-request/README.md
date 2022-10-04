# Function to send request and get response

A PHP Cloud Function for getting the response from request.

_Example input:_

```json
{
    "url":"https://demo.appwrite.io/v1/locale/countries/eu",
    "method":"GET",
    "headers":{
        "x-client-version":"1.0.0"
    },
    "body":""
}
```

_Example output:_


```json
{
    "success":true,
    "response":{
        "headers":"{headers_content}",
        "code":200,
        "body":"{body_content}"
        }
```

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd php/send-http-request
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/php:8.1 sh /usr/local/src/build.sh
```

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.php --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/php:8.1 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `GET` request with appropriate authorization headers. To learn more about runtime, you can visit PHP runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/php-8.1).

## 📝 Notes
 - This example is compatible with PHP 8.1. Other versions may work but are not guaranteed to work as they haven't been tested.