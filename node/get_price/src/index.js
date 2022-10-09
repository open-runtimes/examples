
const getPrice= async function (req, res) {

    const AlphavantageAPIkey = req.env.ALPHAVANTAGE_API_KEY;
    const GoldAPIkey = req.env.GOLD_API_KEY; 
    const CoinAPIkey = req.env.COIN_API_KEY;
    const { type } = req.payload.toLowerCase();
    let price,code;
  
    if (!type) {
      throw new Error("type is required");
    }
  
    if ((!AlphavantageAPIkey || AlphavantageAPIkey === "")&&(!GoldAPIkey || GoldAPIkey === "") && (!CoinAPIkey || CoinAPIkey === "")) {
      throw new Error("API Key is required");
    }

    if (type=== "google" || type === "amazon" || type==="ibm" || type==="apple" ||type==="microsoft"||type==="tesla"||type==="meta") {

      switch(type)
      {case "google": code="GOOGL";
      break;
      case "amazon": code="AMZN";
      break;
      case "ibm": code="IBM";
      break;
      case "apple": code="AAPL";
      break;
      case "microsoft": code="MSFT";
      break;
      case "tesla": code="TSLA";
      break;
      case "meta": code="META";
      break;
    default:code=null}
     fetch(`https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${code}&apikey=${AlphavantageAPIkey}`)
     .then(data=>data.json())
     .then(data=>{
      price=data['Global Quote']['02. open'];
      res.staus(200).send({ "success": true, "price": price });
     })
         } 
     else if (type=== "bitcoin" || type === "ethereum" || type === "tether" || type === "cardano" || type === "ripple" || type === "solana" || type === "dogecoin") {
      switch(type)
      {case "bitcoin": code="BTC";
      break;
      case "ethereum": code="ETH";
      break;
      case "tether": code="USDT";
      break;
      case "cardano": code="ADA";
      break;
      case "ripple": code="XRP";
      break;
      case "solana": code="SOL";
      break;
      case "dogecoin": code="DOGE";
      break;
    default:code=null}
   let requestOptions = {
      "method": "GET",
      "headers": {'X-CoinAPI-Key': CoinAPIkey}
    };
    fetch(`https://rest.coinapi.io/v1/exchangerate/${code}/USD`,requestOptions)
    .then(data=>data.json())
    .then(data=>{
     price=data['rate'];
     res.staus(200).send({ "success": true, "price": price });
   });
    } 
    else if (type === "gold" || type === "silver" || type === "palladium" || type === "platinum") {
      switch(type)
      {case "gold": code="XAU";
      break;
      case "silver": code="XAG";
      break;
      case "palladium": code="XPD";
      break;
      case "platinum": code="XPT";
      break;
    default:code=null}
      
    let myHeaders = new Headers();
    myHeaders.append("x-access-token", GoldAPIkey);
    myHeaders.append("Content-Type", "application/json");
    
    let requestOptions = {
      method: 'GET',
      headers: myHeaders,
      redirect: 'follow'
    };
    
    fetch(`https://www.goldapi.io/api/${code}/USD`, requestOptions)
    .then(data=>data.json())
    .then(data=>{
     price=data['price'];
     res.staus(200).send({ "success": true, "price": price });
   });
    } else {
      res.send({ "success": false, "message": "Type is not supported." });
    }
  };
module.exports = getPrice;