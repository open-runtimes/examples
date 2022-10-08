# 🌐 Translate text from one language to another

A Ruby Cloud Function that compresses png images.

_Example input:_

```json
{
    "image": "iVBORw0KGgoA...CYII=", // image encoded in base64
    "provider": "tinypng", // either "tinypng" or "krakenio"
}
```

_Example output:_

```json
{
    "success": True,
    "image": "iVBORw0KGgoA...ggg=="
}
```

## 📝 Environment Variables

> only selected provider's api keys are neccessary, ie. kraken's api keys are not neccessary when choosing tinypng as the provider.

- **TINYPNG_API** - API key for tinypng service
- **KRAKENIO_KEY** - API key for kraken-io service
- **KRAKENIO_SECRET** - API Secret for kraken-io service

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd ruby/compress-image
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/ruby:3.1 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.rb --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/ruby:3.1 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Ruby 3.1. Other versions may work but are not guaranteed to work as they haven't been tested.