# 🌐 Translate text from one language to another

A Ruby Cloud Function for translating text from one language to another using [Google Translate](https://translate.google.com/).

_Example input:_

```json
{
    "text": "Hello from Open Runtimes 👋",
    "source": "en",
    "target": "es"
}
```

_Example output:_

```json
{
    "text": "Hello from Open Runtimes 👋",
    "translation": "Saludos desde Open Runtime 👋"
}
```

## 📝 Environment Variables

No environment variables needed.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd python/convert_phone_number_to_country_name
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

> Make sure to replace `YOUR_API_KEY` without your key.

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Python runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Ruby 3.1. Other versions may work but are not guarenteed to work as they haven't been tested.