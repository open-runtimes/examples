import { sdk } from "./deps.ts";

export default async function (req: any, res: any) {
  if (!req.variables.DEEPGRAM_API_KEY) {
    return res.json({
      success: false,
      message: "The environment variables (DEEPGRAM_API_KEY) are required."
    })
  }

  const {
    fileUrl
  } = JSON.parse(req.payload);

  if (!fileUrl) {
    return res.json({
      success: false,
      message: "The link to the audio file is missing."
    })
  }

  try {
    const deepgram = await sdk.default(req.variables.DEEPGRAM_API_KEY);
    const transcription = await deepgram.transcription.preRecorded({
      url: fileUrl
    }, {
      punctuate: true
    });

    res.json({
      success: true,
      deepgramData: transcription.results
    });
  } catch(e: any) {
    res.json({
      success: false,
      message: e
   });
  }
}