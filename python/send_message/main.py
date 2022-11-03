from .functions.send_sms_twilio import send_sms_twilio
from .functions.send_email_mailgun import send_email_mailgun
from .functions.send_tweet import send_tweet
from .functions.send_message_discord_webhook import send_message_discord_webhook


def main(req, res):
    """Main Function"""

    result = {}

    try:
        payload = req.payload

        payload_type = payload["type"]
        message = payload["message"]

        if payload_type == "SMS":
            reciever = payload["receiver"]
            result = send_sms_twilio(req.variables, reciever, message)
        elif payload_type == "Email":
            reciever = payload["receiver"]
            subject = payload["subject"]
            result = send_email_mailgun(
                req.variables, reciever, message, subject)
        elif payload_type == "Twitter":
            result = send_tweet(req.variables, message)
        elif payload_type == "Discord":
            result = send_message_discord_webhook(req.variables, message)
        else:
            result = {"success": False, "message": "Invalid Type"}

    except Exception as e:

        return res.json({"success": False, "message": str(e)})

    return res.json(result)
