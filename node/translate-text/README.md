# 🌐 Translate text from one language to another

A NodeJS Cloud Function for translating text from one language to another. Includes support for [Google](https://translate.google.com/), [AWS](https://aws.amazon.com/translate/), and [Azure](https://azure.microsoft.com/en-us/products/cognitive-services/translator/)

_Example input:_

```json
{
  "provider": "google",
  "text": "Hello from Open Runtimes",
  "from": "en",
  "to": "es"
}
```

_Example output:_

```json
{
  "response": {
    "message": "Hola desde Open Runtimes",
    "success": true,
    "from": "en"
  },
  "stdout": "",
  "stderr": ""
}
```

## 📝 Variables

#### Translating with `google`

No variables required

#### Translate with `aws`

- **AWS_ACCESS_KEY** - Access key for AWS
- **AWS_SECRET_KEY** - Secret Key for AWS

#### Translating with `azure`

- **TRANSLATOR_KEY** - Translator Key for Azure

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd node/translate_text
```

2. Enter this function folder and build the code:

```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:v2-18.0 sh /usr/local/src/build.sh
```

As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:

```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/index.js -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/node:v2-18.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit NodeJS runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/node-18.0).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with NodeJS 18.0. Other versions may work but are not guarenteed to work as they haven't been tested.
