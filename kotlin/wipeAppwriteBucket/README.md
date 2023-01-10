# wipeAppwriteBucket() - Kotlin

## ⚡ Function Details

Delete all files inside an Appwrite Storage bucket. 
Bucket ID should be received as payload.

Gets list of files in batches and continue until files returned equals files available. 
Appwrite API will return at most 100 records at a time.

Adds a 1 second delay after each batch of deletion to avoid overwhelming the server.

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **APPWRITE_FUNCTION_ENDPOINT** - Endpoint of Appwrite project
- **APPWRITE_FUNCTION_API_KEY** - Appwrite API Key

## Example input:

```json
{
    "bucketId": "profilePictures"
}
```

## Example output:

Successful response:

```json
{
    "success": true
}
```

Error response:

```json
{
    "success": false,
    "message": "Error description"
}
```

## 🚀 Deployment

There are two ways of deploying the Appwrite function, both having the same results, but each using a different process. It is highly recommended to use CLI deployment to achieve the best experience.

### Using CLI

For instrunctions on using the Appwrite CLI check https://appwrite.io/docs/functions#deployFunction

### Manual using tar.gz

Manual deployment has no requirements and uses Appwrite Console to deploy the tag. First, enter the folder of your function. Then, create a tarball of the whole folder and gzip it. After creating `.tar.gz` file, visit Appwrite Console, click on the `Deploy Tag` button and switch to the `Manual` tab. There, set the `entrypoint` to `src/Index.kt`, and upload the file just generated.
