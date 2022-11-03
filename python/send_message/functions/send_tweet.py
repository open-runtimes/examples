import tweepy


def send_tweet(variables, message):
    """Send tweet to Twitter"""

    if not message:
        raise Exception("Missing message")

    consumer_key = variables.get("TWITTER_API_KEY", None)
    consumer_secret = variables.get("TWITTER_API_KEY_SECRET", None)
    access_token = variables.get("TWITTER_ACCESS_TOKEN", None)
    access_token_secret = variables.get("TWITTER_ACCESS_TOKEN_SECRET", None)

    if not consumer_key:
        raise Exception("Missing Twitter consumer key")
    if not consumer_secret:
        raise Exception("Missing Twitter consumer secret")
    if not access_token:
        raise Exception("Missing Twitter access token")
    if not access_token_secret:
        raise Exception("Missing Twitter access token secret")

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
        return {"success": False, "message": str(e)}

    return {
        "success": True,
    }
