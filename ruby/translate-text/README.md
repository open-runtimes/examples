# 🌐 Translate text from one language to another

Welcome to the documentation of this function 👋 We strongly recommend keeping this file in sync with your function's logic to make sure anyone can easily understand your function in the future. If you don't need documentation, you can remove this file.

## Documentation

A simple Ruby Cloud Function for translating text from one language to another using google translate.

_Example input:_

```json
{
    "payload": "{
        \"text\": \"The message body.\",
        \"source\": \"en\",
        \"target\": \"es\"
    }"
}
```

_Example output:_

```json
{
    "translation": "El texto a traducir."
}
```

## 📝 Environment Variables

No environment variables needed.

## 🚀 Deployment

Copy this function folder somewhere and follow the instructions in the [Ruby runtime readme](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/ruby-3.1#readme) to deploy it.

## 📝 Notes
 - This function is designed for use with Open Runtimes. You can learn more about it [here](https://github.com/open-runtimes/open-runtimes).
 - This example is compatible with Ruby 3.0 and 3.1.