# 📧 Sending Welcome Emails using Mailgun's Email API



A sample Dart Cloud Function for sending emails using the Mailgun API.


_Example input:_

```json
{
    "name": "John Doe",
    "email": "johndoe@gmail.com"
}
```


_Example output:_


```
email sent successfully.
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

* **MAILGUN_API_KEY** - API Key for Mailgun 
* **MAILGUN_DOMAIN** - Domain Name from Mailgun

## 🚀 Deployment

Copy this function folder somewhere and follow the instructions in the [Dart runtime README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/dart-2.15#readme) to deploy it.

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with Dart 2.13, 2.14 and 2.15. Other versions may work but are not guarenteed to work as they haven't been tested.