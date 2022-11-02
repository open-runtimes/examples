import requests


def send_sms_twilio(variables, phoneNumber, message):
    """Send SMS using Twilio"""

    if not phoneNumber:
        raise Exception("No phone number provided")

    if not message:
        raise Exception("No message provided")

    accountSID = variables.get("TWILIO_ACCOUNT_SID", None)
    authToken = variables.get("TWILIO_AUTH_TOKEN", None)
    sender = variables.get("TWILIO_SENDER", None)

    if not accountSID:
        raise Exception("Missing Twilio account SID")
    if not authToken:
        raise Exception("Missing Twilio auth token")
    if not sender:
        raise Exception("Missing Twilio sender")

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
        return {"success": False, "error": str(e)}

    return {"success": True}
