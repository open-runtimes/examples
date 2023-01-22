export default async function (req: any, res: any) {
  const { url, headers, method, body } = JSON.parse(req.payload);

  if (!url) {
    throw new Error("url is required");
  }

  if (!method) {
    throw new Error("method is required");
  }

  const response = await fetch(url, { method, headers, body: body ? JSON.stringify(body) : undefined });

  if (response.status !== 200) {
    res.json({ success: false, message: "URL could not be reached." });
  }

  const data = await response.json();

  res.json({
    success: true,
    response: { code: response.status, headers: response.headers, body: JSON.stringify(data) },
  });
}