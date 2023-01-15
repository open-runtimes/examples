const fs = require('fs')
const sdk = require("microsoft-cognitiveservices-speech-sdk");


module.exports = async function(req, res) {
  
  const {
    audio,
    language
  } = JSON.parse(req.payload);
  
  const {
    SPEECH_KEY,
    SPEECH_REGION
  } = JSON.parse(variables);
  

  const wavUrl = `data:audio/wav;base64,${audio}`
  const buffer = Buffer.from(
    wavUrl.split('base64,')[1],  // only use encoded data after "base64,"
    'base64'
  )
  fs.writeFileSync('./audio.wav', buffer)
  console.log(`wrote ${buffer.byteLength.toLocaleString()} bytes to file.`)

  
  const speechConfig = sdk.SpeechConfig.fromSubscription(SPEECH_KEY, SPEECH_REGION);
  speechConfig.speechRecognitionLanguage = "en-US";

  let audioConfig = sdk.AudioConfig.fromWavFileInput(fs.readFileSync("audio.wav"));
    let speechRecognizer = new sdk.SpeechRecognizer(speechConfig, audioConfig);
    let textGenerated;

    speechRecognizer.recognizeOnceAsync(result => {
        switch (result.reason) {
            case sdk.ResultReason.RecognizedSpeech:
                console.log(`RECOGNIZED: Text=${result.text}`);
                textGenerated = result.text;
                break;
            case sdk.ResultReason.NoMatch:
                console.log("NOMATCH: Speech could not be recognized.");
                break;
            case sdk.ResultReason.Canceled:
                const cancellation = sdk.CancellationDetails.fromResult(result);
                console.log(`CANCELED: Reason=${cancellation.reason}`);

                if (cancellation.reason == sdk.CancellationReason.Error) {
                    console.log(`CANCELED: ErrorCode=${cancellation.ErrorCode}`);
                    console.log(`CANCELED: ErrorDetails=${cancellation.errorDetails}`);
                    console.log("CANCELED: Did you set the speech resource key and region values?");
                }
                break;
        }
        speechRecognizer.close();
    });


    // console.log(response);
  res.json(textGenerated);
};