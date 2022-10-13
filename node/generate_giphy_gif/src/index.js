const axios = require('axios').default;

module.exports = async function (req, res) {
  const APIkey = req.variables["GIPHY_API_KEY"]

  const {
    search
  } = JSON.parse(req.payload);

  if (!search) {
    throw new Error("search is required");
  }

  if (!APIkey) {
    throw new Error("API key is required");
  }

  const response = await axios.get(`https://api.giphy.com/v1/gifs/search?api_key=${APIkey}&q=${search}&limit=1`, {
    headers: {'Apikey': APIkey}
  })

  if (response.status !== 200) {
    throw new Error(`Status code: ${response.status}, Data: ${response.data}`);
  }

  const dataJson = response.data;

  res.json({
    search: search,
    gif: dataJson["data"][0]['url']
  });
};