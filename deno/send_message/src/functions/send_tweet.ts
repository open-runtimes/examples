import { statusUpdate } from "https://kamekyame.github.io/twitter_api_client/api_v1/tweets/update.ts";

export default async function (env: any, text: string) {
  const consumerKey = env["TWITTER_API_KEY"];
  const consumerSecret = env["TWITTER_API_KEY_SECRET"];
  const accessToken = env["TWITTER_ACCESS_TOKEN"];
  const accessTokenSecret = env["TWITTER_ACCESS_TOKEN_SECRET"];

  const res = await statusUpdate(
    {
      consumerKey: consumerKey,
      consumerSecret: consumerSecret,
      token: accessToken,
      tokenSecret: accessTokenSecret,
    },
    {
      status: text,
    }
  );

  if (res.errors) {
    return {
      success: false,
      error: res.errors[0].message,
    };
  }

  return { success: true };
}
