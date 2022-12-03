# 🔗 Shorten a url

A NodeJS Cloud Function for shorten a url with two providers: 
- [Bitly](https://bitly.com/)
- [Tinyurl](https://tinyurl.com/)

_Example input:_

```json
{ 
    "provider": "bitly", 
    "url": "https://www.youtube.com/watch?v=Par3nEq739o" 
}
```

_Example output:_

```json
{
    "success":true,
    "url":"https:\/\/bit.ly\/3dYizF2"
}
```

## 📝 Variables

A single variable: `BITLY_API_KEY`

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd node/url-shortener
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:v2-18.0 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.js --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/node:v2-18.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit NodeJS runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/node-17.0).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with NodeJS 18.0. Other versions may work but are not guarenteed to work as they haven't been tested.