const { Vonage } = require('@vonage/server-sdk')
const {Auth} = require('@vonage/auth')


module.exports = async function test(req, res) {
 
  const credentials = new Auth({
    apiKey: req.variables["API_KEY"],
    apiSecret: req.variables["API_SECRET"],
  });
  const vonage = new Vonage(credentials, options);


  const {
    from,
    to,
    text,
  } = JSON.parse(req.payload);
  try {
    const resp = await vonage.sms.send({ to, from, text });
    console.log('Message sent successfully'); console.log(resp.messages[0].status);

  } catch (error) {
    console.log(error);
  }


};
 



