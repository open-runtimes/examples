#  Generate QR Code

A Java Cloud Function to generate QR code for the given text.

```json
{
  "text":"https://github.com/open-runtimes"
}
```

_Example output:_


```json
{
  "success": true,
  "message": "iVBORw0KGgoAAAANSUhEUgAAATYAAAE2CAIAAABk3in+AAAFzElEQVR42u3aQXLDMAwEQf7/0/EbXLHEXaDnrpRFoJmLzp+k4I4jkBCVhKiEqCREJSEqISoJUQlRSYhKQlRCVBKiEqKSEJWEqISoJEQlRCUhKglRCVFJiEqISkJUEqISopIQlYSotJ7oGdd/3ve5Z5+bkd341W4giiiiiBoDonYDUUQRRRRRRBFFFFFE7QaiiCKKqDEgajcQRRRRRBFFFFFEU8Y/b2Xn/WW7gSiiiCKKKKKIIooooogiiiiiiBoDonYDUUQRRRRRRBFFFFFE7QaiiCKK6FMjzPyIL/Mvh37UZjcQRRRRRI0BUbuBKKKIImoMiCKKKKKI2g1EEUUUUWNA1G4giiiiiCKKKKKIGkP4aTSeM6KIIooooogiajcQRRRRRI0BUUQRRRRRRBFFFFFEEUXUbiCKKKKIIooooogi+sYUMpkhiiiiiCJqDIjaDUQRRRRRRBFFFFFEEUUUUUQRRdQYEEUUUUQRRRRRRBFFNLHM33wuZTcQNQZE7QaiiCKKqDEgajcQRRRRRI0BUUQRNQZE7QaiiCKKqDEgajcQRRRRRKctVuMHYp6dvRuIIooooohihiiiiHoWUUQRRRRRRD1rNxBFFFFEEcUMUUQR9SyiiCKKKKLbu7Xujb9KiCKKKKJCVIgiiiiiQhRRRBFFVIgiiiiiQhRRRBFFVIgiiuh2orc+tbOU78xo2zn7RhdRRBFFFFFEEUUUUUQRtTqIIoooos4ZUUQRRRRRRBFFFFFEEUUUUUQRnb8625aj8axOZNemjyiiiCKKKKKIIooooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKKIIjqSaOgLF37U1viZXuOzmfuMKKKIIooooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKKIIooooogiimjHyjZeSbcurMyLsvJzSEQRRRRRRBFFFFFEEUUUUUQRRRRRRBFFFFFEEUUUUUQRRRRRRBFFFNGFRBsB31qdzKsh8zQa/+UgiiiiiCKKKKKIIooooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKKIItqxOo0LPe/ZeX8ZUUQRRRRRRBFFFFHPIooooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKLfvbCP6fLXbt6nlLd+M6KIIooooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKKIIooooogiiiiiHctx69IJHX8hs3XXN6KIIooooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKKIIooooogiiiiiVidwsU5ht+aLKKKIIooooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKKIIooooogiiqiewuB9869+RK2s90UUUUQRRRRRRBFF1MoiiqgQRRRRK+t9EUUUUUQRFaKIImplvS+i7x3lvI/LGk8yk8q8qwFRRBFFFFFEEUUUUUQRRRRRRBFFFFFEEUUUUUQRRRRRRBFFFFFEEUUU0ZRXyvzNjZdO5nWGKKKIIooooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKKIIooooogiiuh2oplLmfnsvIvD1YAooogiiiiiiCKKKKKIIooooogiiiiiiCKKKKKIIooooogiiiiiiCKK6C6imW+0jVljiCKKKKKIIooooogiiiiiiCKKKKKIClFEEUUUUUQRRRRRRBFFFFFEEZ1AdNsbzTvJzGsFUUQRRRRRRBFFFFFEEUUUUUQRRRRRRBFFFFFEEUUUUUQRRRRRRBFFFNGUbmEIXY5xp4EooogiiiiiiCKKKKKIIooooogiiqjTQBRRRBFFFFFEEUUUUUQRRRRRRDuINpZ5JTVCuvV5YOO/HEQRRRRRRBFFFFFEEUUUUUQRRRRRRBFFFFFEEUUUUUQRRRRRRBFFFNGZRCUhKiEqCVFJiEqISkJUQlQSopIQlRCVhKiEqCREJSEqISoJUQlRSYhKQlRCVBKikhCVEJWEqISoJEQlISohKglRaU0f2T0S4yR5t0gAAAAASUVORK5CYII="
}
```

_Error Example output:_

```json
{
  "success": false,
  "message": "Required fields are not present: text"
}
```


## 📝 Environment Variables

This function does not require any variables.

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd java/generate_qr_code
```

2. Build the code using Docker:
```
docker run -e OPEN_RUNTIMES_ENTRYPOINT=Index.java --rm --interactive --tty --volume $PWD:/mnt/code openruntimes/java:v4-8.0 sh helpers/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e OPEN_RUNTIMES_SECRET=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/mnt/code/code.tar.gz:ro openruntimes/java:v4-8.0 sh helpers/start.sh "java -jar /usr/local/server/src/function/java-runtime-1.0.0.jar"
```

Your function is now listening on port `3000`, and you can execute it using curl request with appropriate authorization headers. To learn more about runtime, you can visit Java runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/java-18.0).

4. In a new terminal window, execute function:

```
curl http://localhost:3000/ -d '{"text":"https://github.com/open-runtimes"}' -H "x-open-runtimes-secret: secret-key" -H "Content-Type: application/json"
```

## 📝 Notes
- This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
- This example is compatible with Java 18.0. Other versions may work but are not guaranteed to work as they haven't been tested.