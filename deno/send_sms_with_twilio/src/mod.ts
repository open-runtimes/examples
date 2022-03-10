export default async function test(req: any, res: any) {
  const accountSID = req.env["TWILIO_ACCOUNT_SID"];
  const authToken = req.env["TWILIO_AUTH_TOKEN"];
  const sender = req.env["TWILIO_SENDER"];

  const {
    phoneNumber,
    text,
  } = JSON.parse(req.payload);

  let response = await fetch(
    `https://api.twilio.com/2010-04-01/Accounts/${accountSID}/Messages.json`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "Authorization": `Basic ${btoa(`${accountSID}:${authToken}`)}`,
      },
      body: new URLSearchParams({
        To: phoneNumber,
        From: sender,
        Body: text,
      }),
    },
  ).then((res) => res.json()).then((data) => {
    return JSON.stringify(data);
  }).catch(console.error);

    res.send(response);
}