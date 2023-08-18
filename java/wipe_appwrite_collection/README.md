# Appwrite Collection Wiper

Welcome to the documentation of this function 👋 We strongly recommend keeping this file in sync with your function's logic to make sure anyone can easily understand your function in the future. If you don't need documentation, you can remove this file.

## 🤖 Documentation

A Java Cloud Function that wipes all the documents inside a collection.

_Example input:_

```json
{
"databaseId": "stage",
"collectionId": "profiles"
}
```

_Example output (_sucess_):_

```json
{
"success": true,
}
```

_Example output (_failure_):_

```json
{
"success": false,
"message": "Collection not found."
}
```

## 📝 Environment Variables

  APPWRITE_FUNCTION_ENDPOINT  -  Endpoint of your Appwrite server
  APPWRITE_FUNCTION_API_KEY   -  Appwrite API Key
  APPWRITE_FUNCTION_PROJECT_ID - Appwrite project ID. If running on Appwrite, this variable is provided automatically.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

   $ git clone https://github.com/open-runtimes/examples.git && cd examples
   $ cd java/wipe_appwrite_collection

2. Enter this function folder and build the code:

   docker run -e INTERNAL_RUNTIME_ENTRYPOINT=Index.java --rm --interactive --tty --volume $PWD:/usr/code openruntimes/java:v2-18.0 sh /usr/local/src/build.sh
   As a result, a code.tar.gz file will be generated.

3. Start the Open Runtime:

   docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/java:v2-18.0 sh /usr/local/src/start.sh
   Your function is now listening on port 3000, and you can execute it by sending POST request with appropriate authorization headers. To learn more about runtime, you can visit Java runtime README.

4. Execute function:

   curl http://localhost:3000/ -d '{"variables":{"APPWRITE_FUNCTION_ENDPOINT":"YOUR_ENDPOINT","APPWRITE_FUNCTION_PROJECT_ID":"YOUR_PROJECT_ID","APPWRITE_FUNCTION_API_KEY":"YOUR_API_KEY"},"payload":"{\"databaseId\":\"stage\",\"collectionId\":\"profiles\"}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"

### Using CLI

   Make sure you have [Appwrite CLI](https://appwrite.io/docs/command-line#installation) installed, and you have successfully logged into your Appwrite server. To make sure Appwrite CLI is ready, you can use the command `appwrite client --debug` and it should respond with green text `✓ Success`.

   Make sure you are in the same folder as your `appwrite.json` file and run `appwrite deploy function` to deploy your function. You will be prompted to select which functions you want to deploy.

### Manual using tar.gz

   Manual deployment has no requirements and uses Appwrite Console to deploy the tag. First, enter the folder of your function. Then, create a tarball of the whole folder and gzip it. After creating `.tar.gz` file, visit Appwrite Console, click on the `Deploy Tag` button and switch to the `Manual` tab. There, set the `entrypoint` to `src/Index.java`, and upload the file we just generated.
