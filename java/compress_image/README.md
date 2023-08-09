# Compress .png Images

A Java Cloud Function that compresses png images.


`image` and `provider` are recieved from the payload, where `image` is a base64 encoded string and `provider` is either [`tinypng`](https://tinypng.com) or [`krakenio`](https://kraken.io)

_Example input:_

```json
{
  "provider": "tinypng",
  "image": "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAf0lEQVR4nO2Wuw2AMAxEbw1gpMwDDMBcGQpooDKydGVAoXCK6J7k6qyc83MCCFGP/Yz+CkDF4KHmjgowbQF0CKFrCDUiwztqxabHCL0/xwcNhoI2UdsjC8g0mQvaSs1zwkg0uQAsAEaGm9/UPCeU7eMj6loTEpf6ZOQWMxd98gAhZnS6XEZcNQAAAABJRU5ErkJggg==",
    
}
```

> `krakenio` is also a supported provider

_Example output:_

```json
{
    "success": true,
    "image": "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgBAMAAACBVGfHAAAAG1BMVEUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACUUeIgAAAACHRSTlMA8712Sxr5g97cFtUAAAA9SURBVCjPY6Aa6AADfAIcDSA8KoBTgLGVgSFCAEmAqZmBwUIBSYClzTQ4wwE52Cs6OtpR4oFFUciBerEKAP58HnyLtZsYAAAAAElFTkSuQmCC"
}
```

## 📝 Variables

> only selected provider's api keys are neccessary, ie. kraken's api keys are not neccessary when choosing tinypng as the provider.

- **TINYPNG_API** - API key for tinypng service
- **KRAKENIO_KEY** - API key for kraken-io service
- **KRAKENIO_SECRET** - API Secret for kraken-io service

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd java/compress_image
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/java:v2-11.0 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.
3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=Index.java --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/java:v2-11.0 sh /usr/local/src/start.sh
```

4. Execute function:

```shell
curl http://localhost:3000/ -d '{"variables":{"TINYPNG_API":"[YOUR_API_KEY]"},"payload":"{\"provider\":\"tinypng\",\"image\":\"iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAf0lEQVR4nO2Wuw2AMAxEbw1gpMwDDMBcGQpooDKydGVAoXCK6J7k6qyc83MCCFGP/Yz+CkDF4KHmjgowbQF0CKFrCDUiwztqxabHCL0/xwcNhoI2UdsjC8g0mQvaSs1zwkg0uQAsAEaGm9/UPCeU7eMj6loTEpf6ZOQWMxd98gAhZnS6XEZcNQAAAABJRU5ErkJggg==\"}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/java-11.0).

## 📝 Notes
- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Java 11.0. Other versions may work but are not guaranteed to work as they haven't been tested.