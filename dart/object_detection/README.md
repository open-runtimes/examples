# 📷 Object Detection using Cloudmersive Vision API

A Dart Cloud Function for object detection from an image URL.

_Example input:_

```json
{
    "url": "https://picsum.photos/seed/open___runtimes/1000/1000"
}
```

_Example output:_


```json
{
    "url": "https://picsum.photos/seed/open___runtimes/1000/1000",
    "name": "cake",
    "confidence": 0.7977721691131592,
    "x": 21,
    "y": 5,
    "width": 494,
    "height": 333
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

**CLOUDMERSIVE_API_KEY** - Your Cloudmersive API key.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd dart/object_detection
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=lib/main.dart --rm --interactive --tty --volume $PWD:/usr/code openruntimes/dart:2.16 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/dart:2.16 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Dart runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/dart-2.16).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Dart 2.16. Other versions may work but are not guaranteed to work as they haven't been tested. Versions below Dart 2.14 will not work, because Apwrite SDK requires Dart 2.14,