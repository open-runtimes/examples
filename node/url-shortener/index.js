const bitly = require("bitly")
const tinyurl = require("turl")
module.exports = async function(req, res) {
   const {
      provider,
      url
   } = JSON.parse(req.payload)

   switch (provider) {
      case "bitly":
         const bitlyClient = new bitly.BitlyClient(req.variables.BITLY_API_KEY, {});
         bitlyClient.shorten(url).then((url) => {
            res.json({
               success: true,
               url: url.link
            })
         }).catch(() => {
            res.json({
               success: false,
               message: "Entered URL is not a valid URL."
            })
         })

         break;
      case "tinyurl":
         tinyurl.shorten(url).then((url) => {
            res.json({
               success: true,
               url
            })
         }).catch(() => {
            res.json({
               success: false,
               message: "Entered URL is not a valid URL."
            })
         })

         break;
      default:
         res.json({
            success: false,
            message: "The provider is invalid."
         })
   }
}