using Tweetinvi;
using Tweetinvi.Exceptions;

namespace SendMessage.Functions
{
public class TwitterSender
{
    public async static Task<Dictionary<string,object>> SendTweet(Dictionary<string,string> variables, string? message)
    {
        if (string.IsNullOrEmpty(message))
        {
            return new Dictionary<string,object> {{"success", false}, {"message","Missing message"}};
        }

        string consumerKey = variables["TWITTER_API_KEY"];
        string consumerSecret = variables["TWITTER_API_KEY_SECRET"];
        string accessToken = variables["TWITTER_ACCESS_TOKEN"];
        string accessTokenSecret = variables["TWITTER_ACCESS_TOKEN_SECRET"];

        if (string.IsNullOrEmpty(consumerKey))
        {
            return new Dictionary<string,object> {{"success", false}, {"message","Missing Twitter consumer key"}};
        }

        if (string.IsNullOrEmpty(consumerSecret))
        {
            return new Dictionary<string,object> {{"success", false}, {"message","Missing Twitter consumer secret"}};
        }

        if (string.IsNullOrEmpty(accessToken))
        {
            return new Dictionary<string,object> {{"success", false}, {"message","Missing Twitter access token"}};
        }

        if (string.IsNullOrEmpty(accessTokenSecret))
        {
            return new Dictionary<string,object> {{"success", false}, {"message","Missing Twitter access token secret"}};
        }
        var client = new TwitterClient(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        try {
            
            await client.Users.GetAuthenticatedUserAsync();
        }
        catch (TwitterException e)
        {
            Console.WriteLine(e);
            return new Dictionary<string, object> {{"success", false}, {"message", e.ToString()}};
        }
        try{
            await client.Tweets.PublishTweetAsync(message);
            return new Dictionary<string, object> {{"success", true}, {"message", "tweet was sent!"}};
        }
        catch(TwitterException ex)
        {
            return new Dictionary<string, object> {{"success", false}, {"message", ex.ToString()}};
        }
        
        
    }
}
}
