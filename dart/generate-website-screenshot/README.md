# 🖼️ Get Website Screenshot

A Dart Cloud Function for getting website screenshot from the ScreeenlyAPI.

_Example input:_

```json
{
    "url": "https://github.com/appwrite/appwrite"
}
```

_Example output:_

```json
{
    "success": "true",
    "screenshot": "iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAYQoH9hbkTPQAAAABJRU5ErkJggg=="
}
```


_Example input:_

```json
{
    "url": "Not A URL"
}
```

_Example output:_

```json
{
    "success": "false",
    "message": "An internal error occured"
}
```

## 📝 Variables

List of variables used by this cloud function:

**SCREEENLY_API_KEY** - Your Screeenly API key.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd dart/generate-website-screenshot
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=lib/main.dart --rm --interactive --tty --volume $PWD:/usr/code openruntimes/dart:v2-2.16 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/dart:v2-2.16 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Dart runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/dart-2.16).

## 📝 Notes
- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Dart 2.16. Other versions may work but are not guaranteed to work as they haven't been tested. Versions below Dart 2.14 will not work, because Apwrite SDK requires Dart 2.14
