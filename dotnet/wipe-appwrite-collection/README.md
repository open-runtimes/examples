# 🧹 Wipe Appwrite Collection

A .NET Cloud Function that wipes all documents from an Appwrite collection.

_Example payload:_

```json
{
    "databaseId":"stage",
    "collectionId":"profiles"
}
```

_Example output:_


```json
{
    "success":true
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
$ cd dotnet/wipe_appwrite_collection
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty \
  -e INTERNAL_RUNTIME_ENTRYPOINT=Index.cs \
  --volume $PWD:/usr/code \
  openruntimes/dotnet:v2-6.0 sh \
  /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run --rm --interactive --tty \
  -p 3000:3000 \
  -e INTERNAL_RUNTIME_KEY=secret-key \
  -e INTERNAL_RUNTIME_ENTRYPOINT=Index.cs \
  -e APPWRITE_FUNCTION_PROJECT_ID=project-id \
  -e APPWRITE_FUNCTION_ENDPOINT=appwrite-endpoint \
  -e APPWRITE_FUNCTION_API_KEY=secret-key \
  --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro \
  openruntimes/dotnet:v2-6.0 \
  sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit .NET runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/dotnet-6.0).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with .NET 6.0. Other versions may work but are not guaranteed to work as they haven't been tested.
