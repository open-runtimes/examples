import requests


def send_sms_twilio(env, phoneNumber, message):
    """Send SMS using Twilio"""

    if not phoneNumber:
        raise Exception("No phone number provided")

    if not message:
        raise Exception("No message provided")

    accountSID = env.get("TWILIO_ACCOUNT_SID", None)
    authToken = env.get("TWILIO_AUTH_TOKEN", None)
    sender = env.get("TWILIO_SENDER", None)
    if not accountSID or not authToken or not sender:
        raise Exception("Missing Twilio credentials")

    try:
        response = requests.post(
            f"https://api.twilio.com/2010-04-01/Accounts/{accountSID}/Messages.json",
            auth=(accountSID, authToken),
            data={
                "From": sender,
                "To": phoneNumber,
                "Body": message,
            },
        )
        response.raise_for_status()
    except Exception as e:
        print(e)
        return {"success": False, "error": e}

    return {"success": True}
