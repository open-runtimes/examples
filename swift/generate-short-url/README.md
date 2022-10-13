# 🔗 Get Short Url

A sample Swift Cloud Function for sending emails using the Mailgun API.


A sample Swift Cloud Function for generating a Short URL using [tinyurl](https://tinyurl.com/app) and [bitly](https://bitly.com/) APIs


_Example input:_

```json
// For bitly

{
    "provider": "bitly",
    "url": "https://www.google.com"
}
```

```json
// For tinyurl

{
    "provider": "tinyurl",
    "url": "https://www.google.com"
}
```


_Example output:_


```json
// Success

{
    "success": true,
    "url": "https://bit.ly/3yvTt7c",
}
```

```json
// Error

{
    "success": false,
    "message": "Entered URL is not a valid URL."
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

* **BITLY_API_TOKEN** - Token key for bitly api
* **TINYURL_API_TOKEN** - Token key for tinyurl

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd swift/generate-short-url
```

2. Build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/swift:5.5 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/swift:5.5 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Swift runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/swift-5.5).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with NodeJS 5.5.