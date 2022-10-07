# Generates Short URL with Bitly and Tinyurl

A Python Cloud Function that generates short URLs with help of Bitly or Tinyurl.

_Example input:_ 

```json
{
	"provider": "tinyurl", 
	"url": "https://github.com/open-runtimes/examples",
}
```


_Example output:_

```json
{
	"success": true,
	"url": "https://tinyurl.com/abc3df"
}
```


_Example input:_

```json
{
	"provider": "bitly", 
	"url": "https://github.com/open-runtimes/examples",
}
```

_Example output:_

```json
{
	"success": true,
	"url": "https://bit.ly/asd57dd"
}
```


_Example input:_

```json
{
	"provider": "owly", 
	"url": "https://github.com/open-runtimes/examples",
}
```

_Example output:_

```json
{
	"success": false,
	"message": "owly is not a valid provider"
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **TINYURL_API_KEY** - Tinyurl API Key
- **BITLY_API_KEY** - Bitly API Key

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd python/generate-short-url
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