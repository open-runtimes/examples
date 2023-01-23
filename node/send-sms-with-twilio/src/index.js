const axios = require('axios').default;

module.exports = async function test(req, res) {
    const accountSID = req.variables["TWILIO_ACCOUNT_SID"];
    const authToken = req.variables["TWILIO_AUTH_TOKEN"];
    const sender = req.variables["TWILIO_SENDER"];
  
    const {
      phoneNumber,
      text,
    } = JSON.parse(req.payload);
  
    let response = await axios({
        method: 'POST',
        url: `https://api.twilio.com/2010-04-01/Accounts/${accountSID}/Messages.json`,
        auth: {
            username: accountSID,
            password: authToken
        },
        data: new URLSearchParams({
            From: sender,
            To: phoneNumber,
            Body: text
        }),
    }).then((data) => {
      return data;
    }).catch((e) => console.error(e));
  
    res.json(response.data);
};