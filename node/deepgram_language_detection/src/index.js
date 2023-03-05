const { Deepgram } = require("@deepgram/sdk");

module.exports = async (req, res) => {
  const { fileUrl } = JSON.parse(req.payload);
  const {apiKey} = JSON.parse(req.variables["DEEPGRAM_API_KEY"]);

  if (!apiKey) {
    res.json({
      success: false,
      message: "API key is required.",
    });
  }

  if (!fileUrl) {
    res.json({
      success: false,
      message: "File Url is required.",
    });
  }

  const deepgram = new Deepgram(apiKey);

  try {
    const response = await deepgram.transcription.preRecorded(
      { url: fileUrl, mimetype: "audio/wav" },
      { detect_language: true, punctuate: true }
    );

    res.json({
      success: true,
      deepgramData: response,
    });
  } catch (error) {
    res.json({
      success: false,
      message: "Please check file URL and API key.",
      error: error,
    });
  }
};
