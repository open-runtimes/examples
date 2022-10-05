# Generate a PDF invoice

A Deno Cloud Function that Generate a PDF invoice.

_Example input:_

```json
{"currency":"EUR","items":[{"name":"Web development","price":15}],"issuer":"Some\nIssuer","customer":"Some\nCustomer","vat":21}
```
_Example output:_
```json
{"success":true,"invoice":"iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="}
```


## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/generate_invoice
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts --rm --interactive --tty --volume $PWD:/usr/code openruntimes/deno:1.14 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts -e INTERNAL_RUNTIME_KEY=secret-key -e INTERNAL_RUNTIME_ENTRYPOINT=mod.ts --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/deno:1.14 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Deno runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/deno-1.14).
