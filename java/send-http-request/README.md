# 📧 Send HTTP request

A Java Cloud Function for sending http requests.

_Example input:_

```json
{
  "url": "https://google-translate1.p.rapidapi.com/language/translate/v2",
  "method": "POST",
  "headers": {
    "X-RapidAPI-Key": "KEY",
    "X-RapidAPI-Host": "HOST"
  },
  "body": {
    "q": "Ahoj světe!",
    "target": "en",
    "source": "cs"
  }
}
```

_Example output:_

```json
{
    "success":true,
    "response":"{\"data\":{\"translations\":[{\"translatedText\":\"Hello world!\"}]}}"
}
```

## 📝 Input Description

List of input used by this cloud function:

* **url** - URL where request should be sent to 
* **method** - Which method should be sent
* **headers** [optional] - should be string string key value pairs
* **body** [optional] - should be string string key value pairs

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd java/send-http-request
```

2. Enter this function folder and build the code:
   
   ```
   docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts --rm --interactive --tty --volume $PWD:/usr/code openruntimes/java:1.8 sh /usr/local/src/build.sh
   ```
   
   As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
   
   ```
   docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/java:1.8 sh /usr/local/src/start.sh
   ```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Java runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/tests/java-8.0).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Java 1.8 version. Other versions may work but are not guarenteed to work as they haven't been tested.