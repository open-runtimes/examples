export default async function (req: any, res: any) {
  const { provider, url } = JSON.parse(req.payload);

  if (!provider) {
    throw new Error("provider is required");
  }

  if (!url) {
    throw new Error("url is required");
  }

  if (provider === "bitly") {
    const BITLY_TOKEN = req.variables["BITLY_TOKEN"];

    if (!BITLY_TOKEN) {
      throw new Error("BITLY_TOKEN is required");
    }

    const response = await fetch(`https://api-ssl.bitly.com/v4/shorten`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${BITLY_TOKEN}`,
      },
      body: JSON.stringify({
        long_url: url,
      }),
    });

    if (response.status !== 200) {
      res.json({ success: false, message: "Entered URL is not a valid URL." });
      throw new Error(
        `Status code: ${response.status}, Data: ${await response.text()}`
      );
    }

    const data = await response.json();
    res.json({ success: true, url: data["link"] });
  } else if (provider === "tinyurl") {
    const TINYURL_TOKEN = req.variables["TINYURL_TOKEN"];

    if (!TINYURL_TOKEN) {
      throw new Error("TINYURL_TOKEN is required");
    }

    const response = await fetch(`https://api.tinyurl.com/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${TINYURL_TOKEN}`,
      },
      body: JSON.stringify({
        url: url,
      }),
    });

    if (response.status !== 200) {
      res.json({ success: false, message: "Entered URL is not a valid URL." });
      throw new Error(
        `Status code: ${response.status}, Data: ${await response.text()}`
      );
    }

    const data = await response.json();
    res.json({ success: true, url: data.data.tiny_url });
  }
}
