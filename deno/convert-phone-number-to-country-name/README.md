# 📱 Validate phone number and get it's country information

A Deno Cloud Function that figures out country in which a phone number is registered.

_Example input:_

```json
{
    "phoneNumber": "+421957215740"
}
```

> Function can also accept phone numbers with spaces, for instance `+421 957 215 740`.


_Example output:_


```json
{
    "phoneNumber": "+421957215740",
    "phonePrefix": "+421",
    "countryCode": "SK",
    "countryName": "Slovakia"
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **APPWRITE_FUNCTION_ENDPOINT** - Endpoint of your Appwrite server
- **APPWRITE_FUNCTION_API_KEY** - Appwrite API Key
- **APPWRITE_FUNCTION_PROJECT_ID** - Appwrite project ID. If running on Appwrite, this variable is provided automatically.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/convert_phone_number_to_country_name
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=mod.ts --rm --interactive --tty --volume $PWD:/usr/code openruntimes/deno:v2-1.14 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=mod.ts --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/deno:v2-1.14 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Deno runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/deno-1.14).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Deno 1.14. Other versions may work but are not guaranteed to work as they haven't been tested.