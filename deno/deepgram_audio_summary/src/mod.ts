export default async function (req: any, res: any) {
  const apikey = req.variables["DEEPGRAM_API_KEY"];
  let fileUrl = "";

  try {
    fileUrl = JSON.parse(req.payload)["fileUrl"];
  } catch (error) {
    return res.json("Invalid JSON string");
  }

  if (!apikey || !fileUrl) {
    return res.json({
      success: false,
      message: "Provide valid fileUrl and valid apikey",
    });
  }

  const response = await fetch(
    "https://api.deepgram.com/v1/listen?",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Token ${apikey}`,
      },
      body: JSON.stringify({ url: fileUrl }),
    }
  );

  if (response.status !== 200) {
    if (response.status === 401) {
      return res.json({
        success: false,
        message: `Encountered an error trying to make a request: ${response.statusText}`,
      });
    }

    const error = await response.json();
    return res.json({
      success: false,
      message: error.reason,
    });
  }

  const data = await response.json();

  return res.json({
    success: true,
    deepgramData: data,
  });
}