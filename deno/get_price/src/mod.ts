export default async function (req: any, res: any) {
  const CoinAPIkey = req.variables["COIN_API_KEY"];
  const AlphavantageAPIkey = req.variables["ALPHAVANTAGE_API_KEY"];
  const GOLDAPIkey = req.variables["GOLD_API_KEY"];

  const payload = JSON.parse(req.payload);

  if (!payload.type) {
    res.json({ success: false, message: 'type is required' });
    return;
  }

  const type = payload.type.toLowerCase();

  let price;
  if (type === "google" || type === "amazon") {
    if (!AlphavantageAPIkey || AlphavantageAPIkey === "") {
      res.json({ success: false, message: 'AlphavantageAPI key is required' });
      return;
    }

    let code = type === "google" ? "GOOGL" : "AMZN";
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

    if (response.status >= 400) {
      res.json({ success: false, message: `${await response.text()}` });
      return;
    }

    const data = await response.json();
    let price = data[str][Object.keys(data[str])[0]]["1. open"];
    
    res.send({ "success": true, "price": price });
  } else if (
    type === "bitcoin" || type === "ethereum"
  ) {
    if (!CoinAPIkey || CoinAPIkey === "") {
      res.json({ success: false, message: 'CoinAPI key is required' });
      return;
    }

    let code = type === "bitcoin" ? "BTC" : "ETH";
    let response: { [rate: string]: any } = await fetch(
      `https://rest.coinapi.io/v1/exchangerate/${code}/USD`,
      {
        method: "GET",
        headers: {
          "X-CoinAPI-Key": CoinAPIkey,
        },
      },
    );

    if (response.status >= 400) {
      res.json({ success: false, message: `${await response.text()}` });
      return;
    }

    const data = await response.json();
    price = data["rate"];
    res.send({ "success": true, "price": price });
  } else if (type === "gold" || type === "silver") {
    if (!GOLDAPIkey || GOLDAPIkey === "") {
      res.json({ success: false, message: 'GOLDAPIkey key is required' });
      return;
    }

    let code = type === "gold" ? "XAU" : "XAG";
    let response: { [index: string]: any } = await fetch(
      `https://www.goldapi.io/api/${code}/USD/`,
      {
        method: "GET",
        headers: {
          "x-access-token": GOLDAPIkey,
        },
      },
    );

    if (response.status >= 400) {
      res.json({ success: false, message: `${await response.text()}` });
      return;
    }
    
    const data = await response.json();

    let price = data["price"];

    res.send({ "success": true, "price": price });
  } else {
    res.send({ "success": false, "message": "Type is not supported." });
  }
}
