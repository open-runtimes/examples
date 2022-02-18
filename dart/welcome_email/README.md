# 📧 Sending Welcome Emails using Mailgun's Email API

<!-- Give your function a name -->

Welcome to the documentation of this function 👋 We strongly recommend keeping this file in sync with your function's logic to make sure anyone can easily understand your function in the future. If you don't need documentation, you can remove this file.

## 🤖 Documentation

A sample Dart Cloud Function for sending a welcome email to a newly registered user.

<!-- Update with your description, for example 'Create Stripe payment and return payment URL' -->

_Example input:_

This function expects no input

<!-- If input is expected, add example -->

_Example output:_

<!-- Update with your expected output -->

```
email sent successfully.
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

* **MAILGUN_API_KEY** - API Key for Mailgun 
* **MAILGUN_DOMAIN** - Domain Name from Mailgun
<!-- Add your custom environment variables -->

## 🚀 Deployment

There are two ways of deploying the Appwrite function, both having the same results, but each using a different process. We highly recommend using CLI deployment to achieve the best experience.

### Appwrite:
#### Using CLI

Make sure you have [Appwrite CLI](https://appwrite.io/docs/command-line#installation) installed, and you have successfully logged into your Appwrite server. To make sure Appwrite CLI is ready, you can use the command `appwrite client --debug` and it should respond with green text `✓ Success`.

Make sure you are in the same folder as your `appwrite.json` file and run `appwrite deploy function` to deploy your function. You will be prompted to select which functions you want to deploy.

#### Manual using tar.gz

Manual deployment has no requirements and uses Appwrite Console to deploy the tag. First, enter the folder of your function. Then, create a tarball of the whole folder and gzip it. After creating `.tar.gz` file, visit Appwrite Console, click on the `Deploy Tag` button and switch to the `Manual` tab. There, set the `entrypoint` to `src/index.js`, and upload the file we just generated.

### Open-Runtimes:

Copy this function folder somewhere and follow the instructions in the [Dart runtime readme](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/dart-2.15#readme) to deploy it.

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions and may be hard to run on your local runtime.