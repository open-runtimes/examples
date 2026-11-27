# 💻 Collection Wiper

A Java Cloud Function for generating a Short URL using [tinyurl](https://tinyurl.com/app) and [bitly](https://bitly.com/)

Supported providers: tinyurl, bitly

_Bitly Example input:_



```json
{"databaseId":"stage","collectionId":"profiles"}
```

_Example output:_


```json
{"success":true}
```

_Error Example output:_

```json
{"success":false,"message":"Collection not found."}
```


## 📝 Environment Variables

List of environment variables used by this cloud function. These must be passed within 'variables' section of the request:

**BITLY_API_KEY** - Pass this API key if the requested provider is bitly. This API key would be the access token used by bitly for generating a short URL.

**TINYURL_API_KEY** - Pass this API key if the requested provider is tinyurl. This API key would be the access token used by tinyurl for generating a short URL.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd java/short_url_generator
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=Index.java --rm -it -v ${PWD}:/usr/code openruntimes/java:v2-11.0 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/java:v2-11.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Java runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/java-11.0).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Java 11.0. Other versions may work but are not guarenteed to work as they haven't been tested.