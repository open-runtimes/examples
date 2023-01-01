const axios = require("axios").default;

module.exports = async (req, res) => {
  let fileUrl;
  try {
    const payload = JSON.parse(req.payload);
    fileUrl = payload.fileUrl;
  } catch (err) {
    res.json({
      success: false,
      message: "Payload does not have a proper structure.",
    });
    return;
  }
  let deepgramApiKey = req.variables["DEEPGRAM_API_KEY"];

  //checks
  if (!deepgramApiKey) {
    res.json({
      success: false,
      message: "Please provide Deepgram Api Key as an environment variable.",
    });
    return;
  }
  if (!fileUrl) {
    res.json({
      success: false,
      message: "Please provide a valid file URL.",
    });
    return;
  }
  //setting headers
  let headers = {
    "content-type": "application/json",
    Authorization: `Token ${deepgramApiKey}`,
  };

  //setting our file url
  let data = JSON.stringify({
    url: fileUrl,
  });

  //making the request
  try {
    const response = await axios.post(
      `https://api.deepgram.com/v1/listen?model=video`,
      data,
      {
        headers: headers,
      }
    );
    if (response.status !== 200) {
      res.json({
        success: false,
        message: response.data,
      });
      return;
    }
    res.json({
      success: true,
      deepgramData: response.data,
    });
  } catch (error) {
    res.json({ success: false, message: error.toString() });
  }
};
