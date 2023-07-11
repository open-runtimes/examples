using System;
using Tweetinvi;

public class TwitterSender
{
    public static void SendTweet(string message)
    {
        if (string.IsNullOrEmpty(message))
        {
            throw new Exception("Missing message");
        }

        string consumerKey = Environment.GetEnvironmentVariable("TWITTER_API_KEY");
        string consumerSecret = Environment.GetEnvironmentVariable("TWITTER_API_KEY_SECRET");
        string accessToken = Environment.GetEnvironmentVariable("TWITTER_ACCESS_TOKEN");
        string accessTokenSecret = Environment.GetEnvironmentVariable("TWITTER_ACCESS_TOKEN_SECRET");

        if (string.IsNullOrEmpty(consumerKey))
        {
            throw new Exception("Missing Twitter consumer key");
        }

        if (string.IsNullOrEmpty(consumerSecret))
        {
            throw new Exception("Missing Twitter consumer secret");
        }

        if (string.IsNullOrEmpty(accessToken))
        {
            throw new Exception("Missing Twitter access token");
        }

        if (string.IsNullOrEmpty(accessTokenSecret))
        {
            throw new Exception("Missing Twitter access token secret");
        }

        Auth.SetUserCredentials(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        Tweet.PublishTweet(message);
    }
}
