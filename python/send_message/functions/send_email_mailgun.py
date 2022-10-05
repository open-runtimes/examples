import requests
import os


def send_email_mailgun(env, email, message, subject):
    """Send email using Mailgun"""

    if not email or not message or not subject:
        raise Exception("Missing email, message or subject")

    domain = env.get("MAILGUN_DOMAIN", None)
    api_key = env.get("MAILGUN_API_KEY", None)

    if not domain or not api_key:
        raise Exception("Missing Mailgun credentials")

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
        response.raise_for_status()

    except Exception as e:
        print(e)
        return {"success": False, "error": e}

    return {
        "success": True,
    }
