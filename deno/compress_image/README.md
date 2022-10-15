# 🔗 Compressing an image

A Deno Cloud Function for compressing an image with two providers: 
- [Tinypng](https://tinypng.com/)
- [Kraken.io](https://kraken.io/)

_Example input:_

```json
{ 
    "provider": "tinypng", 
    "image": "https://www.youtube.com/watch?v=Par3nEq739o" 
}
```

_Example output:_

```json
{
    "success":true,
    "image":"https:\/\/dl.kraken.io\/api\/3a\/4c\/93\/41fb5a7b65703c40965b7b727f\/9c75adab-2253-4d0f-ae31-09c94f265238.png"
}
```

## 📝 Environment Variables

For the provider Tinypng:
- TINIFY_API_KEY

For the provider Kraken.io:
- KRAKEN_API_KEY
- KRAKEN_API_SECRET

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/compress_image
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=mod.ts --rm --interactive --tty --volume $PWD:/usr/code openruntimes/deno:1.24 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=mod.ts --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/deno:1.24 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Deno runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/deno-1.24).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Deno 1.24. Other versions may work but are not guaranteed to work as they haven't been tested.