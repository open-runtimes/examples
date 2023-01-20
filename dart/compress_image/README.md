# 🌐 Compress any Image using tinyPNG and kraken

A Dart Cloud Function that perform a lossless compression of an image. Input image is in base64 format.

_Example input 1:_

```json
{
    "provider":"tinypng",
    "image":"iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAf0lEQVR4nO2Wuw2AMAxEbw1gpMwDDMBcGQpooDKydGVAoXCK6J7k6qyc83MCCFGP/Yz+CkDF4KHmjgowbQF0CKFrCDUiwztqxabHCL0/xwcNhoI2UdsjC8g0mQvaSs1zwkg0uQAsAEaGm9/UPCeU7eMj6loTEpf6ZOQWMxd98gAhZnS6XEZcNQAAAABJRU5ErkJggg=="
}
```

_Example output 1:_

```json
{
    "success":true,
    "image":"iVBORw0KGgoAAAANSUhEUgAAACAAAAAgBAMAAACBVGfHAAAAG1BMVEUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACUUeIgAAAACHRSTlMA8712Sxr5g97cFtUAAAA9SURBVCjPY6Aa6AADfAIcDSA8KoBTgLGVgSFCAEmAqZmBwUIBSYClzTQ4wwE52Cs6OtpR4oFFUciBerEKAP58HnyLtZsYAAAAAElFTkSuQmCC"
}
```

_Example input 2:_

```json
{
    "provider": "tinypng",
    "image": "wrong-image"
}
```

_Example output 3:_

```json
{
    "success": false,
    "message": "Payload is invalid"
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

* **TINYPNG_API_KEY** - API Key for [tinyPNG](https://tinypng.com/developers)
<!-- * **TINYURL_API_KEY** - API Key for TinyUrl -->

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```shell
git clone https://github.com/open-runtimes/examples.git && cd examples
cd dart/compress_image
```

2. Enter this function folder and build the code:

```shell
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=lib/main.dart --rm --interactive --tty --volume $PWD:/usr/code openruntimes/dart:v2-2.16 sh /usr/local/src/build.sh
```

As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

```shell
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/dart:v2-2.16 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Dart runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/dart-2.16).

4. Execute function:

```shell
curl http://localhost:3000/ -d '{"variables":{"TINYPNG_API_KEY":"YOUR_TINYPNG_API_KEY"},"payload":"{\"provider\":\"tinypng\",\"image\":\"iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAf0lEQVR4nO2Wuw2AMAxEbw1gpMwDDMBcGQpooDKydGVAoXCK6J7k6qyc83MCCFGP/Yz+CkDF4KHmjgowbQF0CKFrCDUiwztqxabHCL0/xwcNhoI2UdsjC8g0mQvaSs1zwkg0uQAsAEaGm9/UPCeU7eMj6loTEpf6ZOQWMxd98gAhZnS6XEZcNQAAAABJRU5ErkJggg==\"}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

## 📝 Notes

* This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
* This example is compatible with Dart 2.16. Other versions may work but are not guaranteed to work as they haven't been tested. Versions below Dart 2.14 will not work, because Apwrite SDK requires Dart 2.14,
