export default async function (req: any, res: any) {
  const CoinAPIkey = req.env["COIN_API_KEY"];
  const AlphavantageAPIkey = req.env["ALPHAVANTAGE_API_KEY"];
  const GOLDAPIkey = req.env["GOLD_API_KEY"];

  const { type } = req.payload;

  if (!type) {
    throw new Error("type is required");
  }

  if (!CoinAPIkey || CoinAPIkey === "") {
    throw new Error("CoinAPI key is required");
  }

  if (!AlphavantageAPIkey || AlphavantageAPIkey === "") {
    throw new Error("AlphavantageAPI key is required");
  }

  if (!GOLDAPIkey || GOLDAPIkey === "") {
    throw new Error("GOLDAPIkey key is required");
  }

  let price;
  if (type.toLowerCase() === "google" || type.toLowerCase() === "amazon") {
    let code = type.toLowerCase() === "google" ? "GOOGL" : "AMZN";
    const str = "Time Series (5min)";
    let response: { [str: string]: any } = await fetch(
      `https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=${code}&interval=5min&apikey=${AlphavantageAPIkey}`,
      {
        method: "GET",
        headers: {
          "User-Agent": "request",
        },
      },
    );
    const data = await response.json();
    let price = data[str][Object.keys(data[str])[0]]["1. open"];
    
    res.send({ "success": true, "price": price });
  } else if (
    type.toLowerCase() === "bitcoin" || type.toLowerCase() === "ethereum"
  ) {
    let code = type.toLowerCase() === "bitcoin" ? "BTC" : "ETH";
    let response: { [rate: string]: any } = await fetch(
      `https://rest.coinapi.io/v1/exchangerate/${code}/USD`,
      {
        method: "GET",
        headers: {
          "X-CoinAPI-Key": CoinAPIkey,
        },
      },
    );
    const data = await response.json();
    price = data["rate"];
    res.send({ "success": true, "price": price });
  } else if (type.toLowerCase() === "gold" || type.toLowerCase() === "silver") {
    
    let code = type.toLowerCase() === "gold" ? "XAU" : "XAG";
    let response: { [index: string]: any } = await fetch(
      `https://www.goldapi.io/api/${code}/USD/`,
      {
        method: "GET",
        headers: {
          "x-access-token": GOLDAPIkey,
        },
      },
    );
    const data = await response.json();

    let price = data["price"];

    res.send({ "success": true, "price": price });
  } else {
    res.send({ "success": false, "message": "Type is not supported." });
  }
}
