# 📷 Screenshot API

A Python Cloud Function to take a screenshot of an API

_Example input:_

```json
{
    "env": 
    { 
        "API_FLASH_API_KEY": "<YOUR KEY>" 
    },
    "payload": 
    { 
        "url": "https://www.bruteforced.dev/" 
    }
}
```

_Example output:_


```json
{
    "success": true,
    "screenshot": "iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="
}
```

>Note that the input JSON has to be a string when embedding as a part of the request's body. Here's what that looks like if you're using a tool like Postman or even cURL

```json
{
    "env": "{ \"API_FLASH_API_KEY\": \"<YOUR KEY>\" }",
    "payload": "{ \"url\": \"https://www.bruteforced.dev/\" }"
}
```

>Also, please make sure to replace `YOUR KEY` with the environment variable mentioned below.

## 📝 Environment Variables

List of environment variables used by this cloud function:

**API_FLASH_API_KEY** - Your [API Flash](https://apiflash.com/) access token.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd python/screenshot-website
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/python:3.10 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=main.py --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/python:3.10 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime and how to send a request to this newly hosted endpoint, please read up on the Python runtime docs [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/python-3.10).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Python 3.10. Other versions may work but are not guaranteed to work as they haven't been tested.