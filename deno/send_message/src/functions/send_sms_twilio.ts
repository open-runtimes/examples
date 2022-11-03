export default async function (
  variables: any,
  phoneNumber: string,
  message: string
) {
  const accountSID = variables["TWILIO_ACCOUNT_SID"];
  const authToken = variables["TWILIO_AUTH_TOKEN"];
  const sender = variables["TWILIO_SENDER"];

  if (!accountSID) {
    return {
      success: false,
      message: "TWILIO_ACCOUNT_SID is not set",
    };
  }
  if (!authToken) {
    return {
      success: false,
      message: "TWILIO_AUTH_TOKEN is not set",
    };
  }
  if (!sender) {
    return {
      success: false,
      message: "TWILIO_SENDER is not set",
    };
  }

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
      message: res.statusText,
    };
  }
  return { success: true };
}
