import { base64Encode, base64Decode } from "./deps.ts"

export default async function (req: any, res: any) {
  const { provider, image } = req.payload
  if(!provider) {
    return res.json({
      success: false,
      message: "The provider is missing from the payload."
    })
  }

  if(!image) {
    return res.json({
      success: false,
      message: "The image is missing from the payload."
    })
  }
  
  switch (provider) {
    case "tinypng": {
      if(!req.variables.TINIFY_API_KEY) {
        return res.json({
          success: false,
          message: "The environment variables (TINIFY_API_KEY) are required."
        })
      }

      const jsonResponse = await fetch("https://api.tinypng.com/shrink", {
        method: "POST",
        headers: {
          "Accept": "*/*",
          "Cache-Control": "no-cache",
          "Authorization": `Basic ${base64Encode("api:" + req.variables.TINIFY_API_KEY)}`
        },
        body: base64Decode(image)
      })

      const jsonData = await jsonResponse.json()
      if(jsonData.error) {
        return res.json({
          success: false,
          message: jsonData.message
        })
      } else {
        const outputResponse = await fetch(jsonData.output.url)
        const outputBuffer = await outputResponse.arrayBuffer()
        const outputData = base64Encode(outputBuffer)
        
        return res.json({
          success: true,
          image: outputData
        })
      }
    }

    case "krakenio": {
      if(!req.variables.KRAKEN_API_KEY || !req.variables.KRAKEN_API_SECRET) {
        return res.json({
          success: false,
          message: "The environment variables (KRAKEN_API_KEY, KRAKEN_API_SECRET) are required."
        })
      }

      const formData = new FormData()
      formData.append("data", JSON.stringify({
        auth: {
          api_key: req.variables.KRAKEN_API_KEY,
          api_secret: req.variables.KRAKEN_API_SECRET
        },
        wait: true
      }))

      const file = new File([
        new Blob([base64Decode(image)])
      ], "Nameless")
      formData.append("file", file)

      const jsonResponse = await fetch("https://api.kraken.io/v1/upload", {
        method: "POST",
        body: formData
      })

      try {
        const jsonData = await jsonResponse.json()
        if(!jsonData.success) {
          return res.json({ 
            success: false, 
            message: jsonData.message
          })
        }


        const outputResponse = await fetch(jsonData.kraked_url)
        const outputBuffer = await outputResponse.arrayBuffer()
        const outputData = base64Encode(outputBuffer)

        return res.json({ 
          success: true, 
          image: outputData
        })
      } catch(e) {
        return res.json({ 
          success: false, 
          message: e.toString()
        })
      }
    }
      
    default: {
      return res.json({
        success: false,
        message: "The provider is invalid."
     })
    }
  }
}