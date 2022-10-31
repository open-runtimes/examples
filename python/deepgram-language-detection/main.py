import requests
import json


def errorMessage(res, message):
  return res.json({
      "success": False,
      "message": message,
  })


def main(req,res):
  try:
    #fileUrl and DEEPGRAM_API_KEY is required
    file_url = req.payload['fileUrl']
    api_key = req.variables.get('DEEPGRAM_API_KEY')

  except Exception:
    return errorMessage(res, 'Payload must contain fileUrl and DEEPGRAM_API_KEY.')

  # Setting up headers and body data for the requst
  headers = {
    'content-type': "application/json",
    'Authorization': f"Token {api_key}"
  }
  data = {"url":  file_url }
  try:
    r = requests.post("https://api.deepgram.com/v1/listen?detect_language=true&punctuate=true", headers=headers, json=data)
  except Exception as ex:
    return errorMessage(res, str(ex))

  status = r.status_code
  if status == requests.codes.ok:
    return res.json({
      "success": True,
      "deepgramData": r.json()["results"]
    })
  else:
    return errorMessage(res, f"{status} status error.")