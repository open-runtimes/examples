const { Deepgram } = require("@deepgram/sdk");

module.exports = async (req, res) => {
  const apiKey = req.variables["DEEPGRAM_API_KEY"];
  const fileUrl = req.payload["fileUrl"];

  if (!apiKey) {
    throw new Error("API key is required");
  }

  if (!fileUrl) {
    throw new Error("wav file url is required");
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
      message: "Please provide a valid file URL.",
    });
  }
};
