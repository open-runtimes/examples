# Wipe an Appwrite collection

A PHP Cloud Function that wipes out all the documents inside a collection.

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
    "success": true,
    "sum": 2
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
$ cd php/wipe_appwrite_collection
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/php:v2-8.1 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=index.php --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/php:v2-8.1 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit PHP runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/php-8.1).

4. Execute function:

```
curl http://localhost:3000/ -d '{"variables":{"APPWRITE_FUNCTION_ENDPOINT":"YOUR_ENDPOINT","APPWRITE_FUNCTION_PROJECT_ID":"YOUR_PROJECT_ID","APPWRITE_FUNCTION_API_KEY":"YOUR_API_KEY"},"payload":"{\"databaseId\":\"stage\",\"collectionId\":\"profiles\"}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

## 🚀 Deployment using Appwrite

1. Clone this repository, and enter this function folder:
```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd php/wipe_appwrite_collection
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/php:v2-8.1 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Add a new function to appwrite server. Upload the code.tar.gz file manually. Set index.php as entrypoint.

4. Execute the function. Pass collection ID and database ID as shown in example input and execute the function.


## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with PHP 8.1. Other versions may work but are not guaranteed to work as they haven't been tested.