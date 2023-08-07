using Tweetinvi;


namespace sendMessage.functions;

public class TwitterSender
{
    public async static Task<Dictionary<string,object>> SendTweet(Dictionary<string,string> variables, string message)
    {
        if (string.IsNullOrEmpty(message))
        {
            throw new Exception("Missing message");
        }

        string consumerKey = variables["TWITTER_API_KEY"];
        string consumerSecret = variables["TWITTER_API_KEY_SECRET"];
        string accessToken = variables["TWITTER_ACCESS_TOKEN"];
        string accessTokenSecret = variables["TWITTER_ACCESS_TOKEN_SECRET"];

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
        try {
            var client = new TwitterClient(consumerKey, consumerSecret, accessToken, accessTokenSecret);
            await client.Users.GetAuthenticatedUserAsync();
            var tweet = await client.Tweets.PublishTweetAsync(message);
            return new Dictionary<string, object>{{"success", true}, {"message", tweet}};
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return new Dictionary<string, object>{{"success", false}, {"message", e.Message}};
        }
        
        
    }
}
