import send_email_mailgun from "./functions/send_email_mailgun.ts";
import send_sms_twilio from "./functions/send_sms_twilio.ts";
import send_message_discord_webhook from "./functions/send_message_discord_webhook.ts";
import send_tweet from "./functions/send_tweet.ts";

export default async function (req: any, res: any) {
  const type: string = req.payload["type"];

  const receiver = req.payload?.["receiver"];
  const message = req.payload?.["message"];
  const subject = req.payload?.["subject"];

  let response: any;

  switch (type) {
    case "EMAIL":
      response = await send_email_mailgun(req.env, receiver, message, subject);
      break;

    case "SMS":
      response = await send_sms_twilio(req.env, receiver, message);
      break;

    case "DISCORD":
      response = await send_message_discord_webhook(req.env, message);
      break;

    case "TWITTER":
      response = await send_tweet(req.env, message);
      break;

    default:
      console.log("Invalid type");
      response = {
        success: false,
        error: "Invalid type",
      };
      break;
  }

  res.send(response);
}
