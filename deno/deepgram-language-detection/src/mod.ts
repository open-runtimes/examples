export default async function (req: any, res: any) {
  const APIkey = req.variables["DEEPGRAM_API_KEY"];
  const fileUrl = req.payload["fileUrl"];

  if (!APIkey) {
    return res.json({
      success: false,
      message: "DEEPGRAM_API_KEY is not set",
    });
  }

  if (!fileUrl){
    return res.json({
      success: false,
      message: "fileUrl is not provided",
    });
  }

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
    if (response.status === 401) {
      return res.json({
        success: false,
        message: `Status code: ${response.status}, Data: ${response.statusText} - Please check your Deepgram API key`,
      });
    }

    const error = await response.json();
    return res.json({
      success: false,
      message: error.reason,
    });
  }

  const data = await response.json();

  res.json({
    success: true,
    deepgramData: data,
  });
}
