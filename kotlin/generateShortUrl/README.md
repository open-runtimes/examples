# generateShortUrl() - Kotlin

## ⚡ Function Details

Generate a short URL.
Introduce support for bitly and tinyurl APIs.

If necessary, introduce their secret keys as function variables. URL and provider to use must be provided as payload, and shortened URL should be returned.

## Example input:

```json
{ 
  "provider":"bitly",
  "url":"https://google.com/",
  "provider_api_key": "--the api key from the provider--"
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

```json
{
  "success":false,
  "message":"Payload doesn't contain valid provider - 'random'"
}
```

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd kotlin/generateShortUrl
```

2. Build the code:

```bash
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/Index.kt --rm --interactive --tty --volume $PWD:/usr/code openruntimes/kotlin:v2-1.6 sh /usr/local/src/build.sh
```

3. Spin-up open-runtime:

```bash
docker run -p 3001:3000 -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/kotlin:v2-1.6 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Kotlin runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/kotlin-1.6).

4. Execute command:
```bash
curl -X 'POST' \
'http://localhost:3000/' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-H 'X-Internal-Challenge: secret-key' \
-d '{
"payload": "{\"url\":\"https://facebook.com/test\",\"provider\":\"bitly\",\"provider_api_key\":\"to-be-created\"}"
}'
```
The provider_api_key should be created 


## 📝 Notes

- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
