const axios = require('axios').default;

module.exports = async function (req, res) {
  let countryId = '';
  if (req.payload) {
    countryId = JSON.parse(req.payload).country;
  }

  const response = await axios.get(`https://api.covid19api.com/summary`);

  if (response.status !== 200) {
    throw new Error(`Status code: ${response.status}, Data: ${response.data}`);
  }

  const dataJson = response.data;

  if (countryId) {
    dataJson.Countries = dataJson.Countries.filter(country => 
      country['Country'] === countryId || 
      country['CountryCode'] === countryId || 
      country['Slug'] === countryId);
    }

  let country = dataJson.Countries[0]['Country'] ?? 'The World';
  let confirmedCasesToday = dataJson.Countries[0]['NewConfirmed'] ?? dataJson.Global.NewConfirmed;
  let deathsToday = dataJson.Countries[0]['NewDeaths'] ?? dataJson.Global.NewDeaths;
  let recoveredCasesToday = dataJson.Countries[0]['NewRecovered'] ?? dataJson.Global.NewRecovered;

  res.json({
    country: country,
    confirmedCasesToday: confirmedCasesToday,
    deathsToday: deathsToday,
    recoveredCasesToday: recoveredCasesToday
  });
};