export default async function (env: any, phoneNumber: string, message: string) {
  const accountSID = env["TWILIO_ACCOUNT_SID"];
  const authToken = env["TWILIO_AUTH_TOKEN"];
  const sender = env["TWILIO_SENDER"];

  const res = await fetch(
    `https://api.twilio.com/2010-04-01/Accounts/${accountSID}/Messages.json`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        Authorization: `Basic ${btoa(`${accountSID}:${authToken}`)}`,
      },
      body: new URLSearchParams({
        To: phoneNumber,
        From: sender,
        Body: message,
      }),
    }
  );

  if (res.status !== 201) {
    return {
      success: false,
      error: res.statusText,
    };
  }
  return { success: true };
}
