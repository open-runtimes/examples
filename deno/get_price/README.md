# Get Price!

A Deno Appwrite Function to get the price of crypto, stocks, and commodities. Supported types include Bitcoin, Ethereum, Google, Amazon, gold, and silver.

_Example input:_

```json
{
    "type": "gold"
}
```

> Other allowed types are: `google`, `amazon`, `ethereum`, `bitcoin`, `gold`, `silver`.

_Example output:_


```json
{
    "success":true,
    "price":1666.81
}
```

```json
{
    "success":false,
    "message":"GOLDAPIkey key is required"
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **COIN_API_KEY** - Your [Coin API key](https://docs.coinapi.io/#md-docs)
- **ALPHAVANTAGE_API_KEY** - Your [Alphavantage API key](https://www.alphavantage.co/)
- **GOLD_API_KEY** - Your [Gold API key](https://www.goldapi.io/)

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd deno/get_price
```

2. Enter this function folder and build the code:
```
docker run -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts --rm --interactive --tty --volume $PWD:/usr/code openruntimes/deno:v2-1.21 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/mod.ts -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/deno:v2-1.21 sh /usr/local/src/start.sh
```

4. Execute function:

```
curl http://localhost:3000/ -d '{"variables":{"GOLD_API_KEY":"[YOUR_API_KEY]"},"payload": "{\"type\":\"gold\"}"}' -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json"
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit Deno runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/deno-1.21).