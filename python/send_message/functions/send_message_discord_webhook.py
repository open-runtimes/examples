import json
import requests


def send_message_discord_webhook(variables, message):
    """Send message to Discord webhook"""

    if not message:
        raise Exception("No message provided")

    webhook = variables.get("DISCORD_WEBHOOK_URL", None)

    if not webhook:
        raise Exception("No Discord webhook URL provided")

    try:
        response = requests.post(
            webhook,
            data=json.dumps({"content": message}),
            headers={"Content-Type": "application/json"},
        )
        response.raise_for_status()
    except Exception as e:
        print(e)
        return {"success": False, "error": str(e)}

    return {"success": True}
