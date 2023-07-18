export default async function (req: any, res: any) {
  // try catch everything
  try {
    const apikey = req.variables["DEEPGRAM_API_KEY"];
    const fileUrl = JSON.parse(req.payload)["fileUrl"];

    // error handling
    if (!apikey) {
      return res.json({
        success: false,
        message: "Missing Deepgram API key",
      });
    }

    if (!fileUrl) {
      return res.json({
        success: false,
        message: "Missing fileUrl",
      });
    }

    const response = await fetch(
      "https://api.deepgram.com/v1/listen?model=video",
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

    return res.json({
      success: true,
      deepgramData: data,
    });
  } catch (error) {
    return res.json({
      success: false,
      message: error.message,
    });
  }
}
