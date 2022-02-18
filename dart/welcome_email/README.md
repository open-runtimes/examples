# 📧 Sending Welcome Emails using Mailgun's Email API
A sample Dart Cloud Function for sending a welcome email to a newly registered user.

## 📝 Environment Variables
Go to Settings tab of your Cloud Function. Add the following environment variables.

* **MAILGUN_API_KEY** - API Key for Mailgun 
* **MAILGUN_DOMAIN** - Domain Name from Mailgun

## 🚀 Building and Packaging

To package this example as a cloud function, follow these steps.

* Ensure that your folder structure looks like this 
```
.
├── main.dart
├── .appwrite
└── pubspec.yaml
```

* Create a tarfile

```bash
$ cd ..
$ tar -zcvf code.tar.gz welcome_email
```

* Navigate to the Overview Tab of your Cloud Function > Deploy Tag
* Input the entrypoint of the function (in this case "main.dart") as your entrypoint
* Upload your tarfile 
* Wait for it to build
* Click 'Activate'

## 🎯 Trigger

Head over to your function in the Appwrite console and under the Settings Tab, enable the `users.create` and `account.create` event.
