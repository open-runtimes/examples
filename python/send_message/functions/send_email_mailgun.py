import requests
import os


def send_email_mailgun(variables, email, message, subject):
    """Send email using Mailgun"""

    if not email or not message or not subject:
        raise Exception("Missing email, message or subject")

    domain = variables.get("MAILGUN_DOMAIN", None)
    api_key = variables.get("MAILGUN_API_KEY", None)

    if not domain:
        raise Exception("Missing Mailgun domain")
    if not api_key:
        raise Exception("Missing Mailgun API key")

    try:
        response = requests.post(
            f"https://api.mailgun.net/v3/{domain}/messages",
            auth=("api", api_key),
            data={
                "from": "<welcome@my-awesome-app.io>",
                "to": email,
                "subject": subject,
                "text": message,
            },
        )
        # response.raise_for_status()

    except Exception as e:
        print(e)
        return {"success": False, "error": str(e)}

    return {
        "success": True,
    }
