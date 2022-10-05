import tweepy


def send_tweet(env, message):
    """Send email using Mailgun"""

    if not message:
        raise Exception("Missing message")

    consumer_key = env.get("TWITTER_API_KEY", None)
    consumer_secret = env.get("TWITTER_API_KEY_SECRET", None)
    access_token = env.get("TWITTER_ACCESS_TOKEN", None)
    access_token_secret = env.get("TWITTER_ACCESS_TOKEN_SECRET", None)

    if (
        not consumer_key
        or not consumer_secret
        or not access_token
        or not access_token_secret
    ):
        raise Exception("Missing Twitter credentials")

    try:
        auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
        auth.set_access_token(
            access_token,
            access_token_secret,
        )

        api = tweepy.API(auth)

        api.update_status(message)

    except Exception as e:
        print(e)
        return {"success": False, "error": e}

    return {
        "success": True,
    }
