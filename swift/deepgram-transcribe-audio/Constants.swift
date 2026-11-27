let exampleRequest = """
{
  "payload": {
    "fileUrl": "https://static.deepgram.com/examples/interview_speech-analytics.wav"
  },
  "variables": {
    "DEEPGRAM_API_KEY": "<YOUR_API_KEY>"
  }
}
"""
let deepgramURL = "https://api.deepgram.com/v1/listen"