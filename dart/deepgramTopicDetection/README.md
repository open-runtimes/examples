# 📝 Topic detection using Deepgram

A Dart Cloud Function that detects topics from pre-recorded audio using [Deepgram](https://developers.deepgram.com/).

_Example input 1:_

```json
{
  "payload": "{\"fileUrl\": \"phtts://static.deepgram.com/examples/Bueller-Life-moves-pretty-fast.wav\"}"
}
```

_Example output 1:_

```json
{
  "success": true,
  "deepgramData": { "key": "Object" }
}
```

_Example input 2:_

```json
{
  "payload": "{\"fileUrl\": \"Bueller-Life-moves-pretty-fast.wav\"}"
}
```

_Example output 2:_

```json
{
  "success": false,
  "message": "Please provide a valid file URL."
}
```

_Example input 3:_

```json
{
  "payload": "{}"
}
```

_Example output 3:_

```json
{
  "success": false,
  "message": "Please provide a valid file URL."
}
```

If the API Key is not provided, you would get:

```json
{
  "success": false,
  "message": "Please provide a valid deepgram API key"
}
```

## 📝 Variables

List of variables used by this cloud function:

- **DEEPGRAM_API_KEY** - API Key for Deepgram

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
  