# 🔗 Shorten a url

A Kotlin Cloud Function for shorten a url with two providers:

- [Bitly](https://bitly.com/)
- [Tinyurl](https://tinyurl.com/)

## Example input:

```json
{ 
  "provider":"bitly",
  "url":"https://google.com/",
}
```

## Example output:

Successful response:

```json
{
  "success":true,
  "url":"https://bit.ly/3y3HDkC"
}
```

Error response:

```json
{
  "success":false,
  "message":"Payload does not contain 'provider'"
}
```

```json
{
  "success":false,
  "message":"Payload does not contain 'url'"
}
```

## 📝 Variables

- `PROVIDER_API_KEY`: Bitly or Tinyurl API key

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd kotlin/generate-short-url
```

2. Build the code:

```bash
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/Index.kt --rm --interactive --tty --volume $PWD:/usr/code openruntimes/kotlin:v2-1.6 sh /usr/local/src/build.sh
```

3. Spin-up open-runtime:

```bash
docker run -p 3000:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/kotlin:v2-1.6 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Kotlin runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/kotlin-1.6).

4. Execute command:
```bash
curl -X 'POST' 'http://localhost:3000/' -H 'Content-Type: application/json' -H 'X-Internal-Challenge: secret-key' -d '{"payload": "{\"url\":\"https://appwrite.io/\",\"provider\":\"bitly\"}","variables": {"PROVIDER_API_KEY":"<YOUR_API_KEY>"}}'
```

## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
