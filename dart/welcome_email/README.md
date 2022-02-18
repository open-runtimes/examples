# 📧 Sending Welcome Emails using Mailgun's Email API

<!-- Give your function a name -->

Welcome to the documentation of this function 👋 We strongly recommend keeping this file in sync with your function's logic to make sure anyone can easily understand your function in the future. If you don't need documentation, you can remove this file.

## 🤖 Documentation

A sample Dart Cloud Function for sending a welcome email to a newly registered user.

<!-- Update with your description, for example 'Create Stripe payment and return payment URL' -->

_Example input:_

```json
{
    "name": "John Doe",
    "email": "johndoe@gmail.com"
}
```

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

Copy this function folder somewhere and follow the instructions in the [Dart runtime readme](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/dart-2.15#readme) to deploy it.

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions and may be hard to run on your local runtime.
 - This example is compatible with Dart 2.13, 2.14 and 2.15.