import { statusUpdate } from "https://deno.land/x/twitter_api_client/api_v1/tweets/update.ts";

export default async function (variables: any, text: string) {
  const consumerKey = variables["TWITTER_API_KEY"];
  const consumerSecret = variables["TWITTER_API_KEY_SECRET"];
  const accessToken = variables["TWITTER_ACCESS_TOKEN"];
  const accessTokenSecret = variables["TWITTER_ACCESS_TOKEN_SECRET"];

  if (!consumerKey) {
    return {
      success: false,
      message: "TWITTER_API_KEY is not set",
    };
  }
  if (!consumerSecret) {
    return {
      success: false,
      message: "TWITTER_API_KEY_SECRET is not set",
    };
  }
  if (!accessToken) {
    return {
      success: false,
      message: "TWITTER_ACCESS_TOKEN is not set",
    };
  }
  if (!accessTokenSecret) {
    return {
      success: false,
      message: "TWITTER_ACCESS_TOKEN_SECRET is not set",
    };
  }

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
      message: res.messages[0].message,
    };
  }

  return { success: true };
}
