import { base64Encode } from "./deps.ts";

export default async function (req: any, res: any) {
  const { provider, image } = JSON.parse(req.payload);
  switch (provider) {
    case "tinypng":
      if(!req.variables.TINIFY_API_KEY) {
        return res.json({
          success: false,
          message: "The environment variables (TINIFY_API_KEY) are required."
        })
      }

      try {
        const receiveImage = await fetch(image);
        const requestImage = await fetch("https://api.tinypng.com/shrink", {
          method: "POST",
          headers: {
            "Accept": "*/*",
            "Cache-Control": "no-cache",
            "Authorization": `Basic ${base64Encode("api:" + req.variables.TINIFY_API_KEY)}`
          },
          body: await receiveImage.body
        });
        
        return res.json({ 
          success: true, 
          image: (await requestImage.json()).output.url
        });
      } catch (error) {
        return res.json({
          success: false,
          message: error
        });
      };
    case "krakenio":
      if(!req.variables.KRAKEN_API_KEY || req.variables.KRAKEN_API_SECRET) {
        return res.json({
          success: false,
          message: "The environment variables (KRAKEN_API_KEY, KRAKEN_API_SECRET) are required."
        })
      }

      try {
        const requestImage = await fetch("https://api.kraken.io/v1/url", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
          },
          body: JSON.stringify({
            auth: {
              api_key: req.variables.KRAKEN_API_KEY,
              api_secret: req.variables.KRAKEN_API_SECRET
            },
            wait: true,
            url: image
          })
        });
        
        return res.json({ 
          success: true, 
          image: (await requestImage.json()).kraked_url
        });
      } catch (error) {
        return res.json({
          success: false,
          message: error
        });
      };
    default:
      res.json({
        success: false,
        message: "The provider is invalid."
     });
  };
};