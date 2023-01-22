const bitly = require("bitly")
const tinyurl = require("turl")

module.exports = async function(req, res) {
   const { provider, url } = req.payload
   if (!provider) {
      return res.json({
        success: false,
        message: "The provider is missing."
      })
   }

   if (!url) {
      return res.json({
         success: false,
         message: "The link is missing."
      })
   }

   switch (provider) {
      case "bitly": {
         const bitlyClient = new bitly.BitlyClient(req.variables.BITLY_API_KEY, {})
         bitlyClient.shorten(url)
            .then((url) => {
               return res.json({
                  success: true,
                  url: url.link
               })
            })
            .catch((e) => {
               return res.json({
                  success: false,
                  message: e.toString()
               })
            })

            break
      }

      case "tinyurl": {
         tinyurl.shorten(url)
            .then((url) => {
               return res.json({
                  success: true,
                  url
               })
            })
            .catch((e) => {
               return res.json({
                  success: false,
                  message: e.toString()
               })
            })

            break
      }

      default: {
         res.json({
            success: false,
            message: "The provider is invalid."
         })
      }
   }
}