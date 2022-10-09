# 📷 Get Price Function

A Node Cloud Function for getting the price of crypto, stocks, and commodities. Supported goods include (at least) Bitcoin, Ethereum, Google, Amazon, gold, and silver.

_Example input:_

```json
{
    "type": "gold"
}
```

_Example output:_


```json
{
    "success":true,
    "price":1697.6
}
```

## 📝 Environment Variables

List of environment variables used by this cloud function:

- **ALPHAVANTAGE_API_KEY** - Your [Alphavantage API key](https://www.alphavantage.co/)
- **GOLD_API_KEY** - Your [Gold API key](https://www.goldapi.io/)
- **COIN_API_KEY** - Your [Coin API key](https://docs.coinapi.io/#md-docs)

## 🚀 Deployment

1. Clone this repository, and enter this function folder:

```
$ git clone https://github.com/open-runtimes/examples.git && cd examples
$ cd node/get_price
```

2. Enter this function folder and build the code:
```
docker run --rm --interactive --tty --volume $PWD:/usr/code openruntimes/node:17.0 sh /usr/local/src/build.sh
```
As a result, a `code.tar.gz` file will be generated.

3. Start the Open Runtime:
```
docker run -p 3000:3000 -e INTERNAL_RUNTIME_ENTRYPOINT=src/index.js -e INTERNAL_RUNTIME_KEY=secret-key --rm --interactive --tty --volume $PWD/code.tar.gz:/tmp/code.tar.gz:ro openruntimes/node:17.0 sh /usr/local/src/start.sh
```

Your function is now listening on port `3000`, and you can execute it by sending `POST` request with appropriate authorization headers. To learn more about runtime, you can visit NodeJS runtime [README](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/node-17.0).

## 📝 Notes
 - This function is designed for use with Appwrite Cloud Functions. You can learn more about it in [Appwrite docs](https://appwrite.io/docs/functions).
 - This example is compatible with NodeJS 17.0. Other versions may work but are not guarenteed to work as they haven't been tested.