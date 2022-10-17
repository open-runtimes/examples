export default async function (req: any, res: any) {
  const APIkey = req.variables["DEEPGRAM_API_KEY"];
  const fileUrl = req.payload["fileUrl"];

  const response = await fetch(
    "https://api.deepgram.com/v1/listen?model=general&detect_language=true&punctuate=true",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Token ${APIkey}`,
      },
      body: JSON.stringify({ url: fileUrl }),
    }
  );

  if (response.status !== 200) {
    res.json({
      success: false,
      message: `Status code: ${response.status}, Data: ${response.statusText}`,
    });
  }

  const data = await response.json();

  res.json({
    success: true,
    deepgramData: data,
  });
}
