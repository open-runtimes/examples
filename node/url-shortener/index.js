const bitly = require("bitly")
const tinyurl = require("turl")
module.exports = async function(req, res) {
   const {
      provider,
      url
   } = JSON.parse(req.payload)

   switch (provider) {
      case "bitly":
         const bitlyClient = new bitly.BitlyClient(req.env.BITLY_API_KEY, {});
         bitlyClient.shorten(url).then((url) => {
            res.json({
               
            })
         })

         break;
      default:
         break;
   }
}