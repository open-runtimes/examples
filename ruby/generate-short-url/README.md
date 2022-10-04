# Generate Short URL

A Ruby Cloud Function that generates a short URL using a specified provider

_Example input:_

```json
{
  "provider": "bitly",
  "url": "https://google.com/"
}
```

_Example Success output:_

```json
{
  "success": true,
  "url": "https://bit.ly/3y3HDkC"
}
```

_Example Error output from TinyURL:_

```json
{
  "success": false,
  "errors": ["Entered URL is not a valid URL."]
}
```

_Example Error output from Bitly:_

```json
{
  "success": false,
  "errors": [
    {
      "field": "long_url",
      "error_code": "invalid"
    }
  ]
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **BITLY_ACCESS_TOKEN** - bit.ly access token
- **TINYURL_ACCESS_TOKEN** - tiny.url access token

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd ruby/convert_phone_number_to_country_name
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

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Ruby runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1).

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Ruby 3.1. Other versions may work but are not guaranteed to work as they haven't been tested.
