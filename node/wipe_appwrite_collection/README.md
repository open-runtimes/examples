# Wipe an Appwrite collection

A Node Cloud Function that wipes out all the documents inside a collection.

_Example input:_

```json
{
    "databaseId": "stage",
    "collectionId": "profiles"
}
```

_Example output (success):_

```json
{
    "success": true
}
```

_Example output (failure):_

```json
{
    "success": false,
    "message": "Collection not found."
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
$ cd node/wipe_appwrite_collection
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:17.0 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.js --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/node:17.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Node runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/node-17.0).

## 🚀 Deployment using Appwrite

1. Clone this repository, and enter this function folder:
```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd node/wipe_appwrite_collection
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:17.0 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Add a new function to appwrite server. Upload the code.tar.gz file manually. Set index.js as entrypoint.

4. Execute the function. Pass collection ID and database ID as shown in example input and execute the function.


## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Node 17.0. Other versions may work but are not guaranteed to work as they haven't been tested.