# 🦠 Get COVID-19 stats from an API

A sample Swift Cloud Function for fetching data from a REST API, in this case [Covid19Api](https://covid19api.com/).

_Example input:_

```json
{
    "payload": "{
        \"country\": \"NZ\",
    }"
}
```

_Example output:_

```json
{
  "deathsToday" : 0,
  "recoveredToday" : 0,
  "confirmedCasesToday" : 0,
  "country" : "New Zealand"
}
```

## 📝 Environment Variables

No environment variables needed.

## 🚀 Deployment

Copy this function folder somewhere and follow the instructions in the [Swift runtime readme](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/swift-5.5#readme) to deploy it.

## 📝 Notes
 - This function is designed for use with Open Runtimes. You can learn more about it [here](https://github.com/open-runtimes/open-runtimes).
 - This example is compatible with Swift 5.5.