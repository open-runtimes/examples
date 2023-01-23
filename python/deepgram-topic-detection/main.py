import requests
import validators


def main(req, res):
    # validate the request url/payload is not empty
    file_url = None
    try:
        file_url = req.payload['fileUrl']
        api_key = req.variables.get('DEEPGRAM_API_KEY')
    except Exception:
        return res.json({'success': "false", 'message': "Invalid/Empty Payload"})

    if not file_url:
        return res.json({'success': "false", 'message': "Empty URL"})

    # validate if the file url is in correct format
    if not validators.url(file_url):
        return res.json({'success': "false", 'message': "Invalid URL please provide URL in correct format"})

    # Make sure we have environment variables required to execute
    if not api_key:
        return res.json({'success': "false", 'message': "You need to pass an API key for the provider"})

    # Fetch the DEEPGRAM Data
    base_url = "https://api.deepgram.com/v1/listen?detect_topics=true&punctuate=true"
    response = requests.post(base_url, json={"url": file_url},
                             headers={"Content-Type": "application/json", "Authorization": "Token " + api_key})

    # Make sure we get the deepgram data
    if response.status_code != 200:
        return res.json({"success": "false", 'message': "Can't fetch the data/API key is not valid"})

    print(response.json())

    # Return DEEPGRAM DATA
    return res.json({"success": "true", "deepgramData": response.json()["results"]})
