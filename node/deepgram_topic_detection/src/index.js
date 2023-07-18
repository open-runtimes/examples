const { Deepgram } = require("@deepgram/sdk");

module.exports = async (req, res) => {
  const { fileUrl } = JSON.parse(req.payload);
  const apiKey = req.variables["DEEPGRAM_API_KEY"];

  if (!apiKey) {
    res.json({
      success: false,
      message: "API key is required.",
    });
  }

  if (!fileUrl) {
    res.json({
      success: false,
      message: "wav file url is required.",
    });
  }

  const deepgram = new Deepgram(apiKey);

  try {
    const response = await deepgram.transcription.preRecorded(
      { url: fileUrl, mimetype: "audio/wav" },
      { detect_topics: true, punctuate: true }
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
