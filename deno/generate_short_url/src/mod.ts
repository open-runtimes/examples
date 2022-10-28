export default async function (req: any, res: any) {
  const { provider, url } = JSON.parse(req.payload);

  if (!provider) {
    res.json({ success: false, message: "provider is required" });
    return;
  }

  if (!url) {
    res.json({ success: false, message: "url is required" });
    return;
  }

  if (provider === "bitly") {
    const BITLY_TOKEN = req.variables["BITLY_TOKEN"];

    if (!BITLY_TOKEN) {
      res.json({ success: false, message: "BITLY_TOKEN is required"});
      return;
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
      res.json({ success: false, message: "TINYURL_TOKEN is required"});
      return;
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
