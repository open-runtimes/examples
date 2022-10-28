# generateShortUrl

Welcome to the documentation of this function 👋 

## 🤖 Documentation

Generates a short URL using the `bitly` and `tinyurl` APIs.

_Example payload:_

```json
{
    "provider":"bitly",
    "url":"https://google.com/"
}
```

_Example successful response:_

```json
{
    "success":true,
    "url":"https:\/\/bit.ly\/3DGAJF8"
}
```

_Example failure response:_

```json
{
    "success":false,
    "message":"The value provided is invalid."
}
```

## 📝 Environment Variables

This cloud function needs the following environment variables:

- `BITLY_API_KEY`: Bitly API Access Token
- `TINYURL_API_KEY`: Tinyurl API Key

ℹ️ *Note: Learn how to create the [Bitly](https://dev.bitly.com/) and [Tinyurl](https://tinyurl.com/app/dev) API Keys via their docs.*

## 🚀 Deployment

There are two ways of deploying the Appwrite function, both having the same results, but each using a different process. We highly recommend using CLI deployment to achieve the best experience.

### Using CLI

Make sure you have [Appwrite CLI](https://appwrite.io/docs/command-line#installation) installed, and you have successfully logged into your Appwrite server. To make sure Appwrite CLI is ready, you can use the command `appwrite client --debug` and it should respond with green text `✓ Success`.

Make sure you are in the same folder as your `appwrite.json` file and run `appwrite deploy function` to deploy your function. You will be prompted to select which functions you want to deploy.

### Manual using tar.gz

Manual deployment has no requirements and uses Appwrite Console to deploy the tag. First, enter the folder of your function. Then, create a tarball of the whole folder and gzip it. After creating `.tar.gz` file, visit Appwrite Console, click on the `Deploy Tag` button and switch to the `Manual` tab. There, set the `entrypoint` to `src/Index.cs`, and upload the file we just generated.
