import json
import requests
from json import JSONDecodeError


def main(req, res):
    url = None

    # Extracting URL from the payload
    try:
        payload = json.loads(req.payload)
        url = payload['fileUrl']

    except KeyError:
        err = 'Please provide all the required parameters.'
        return res.json({
            "success": False,
            "message": err
        })

    # Checking if DEEPGRAM_API_KEY is provided
    if not req.variables.get('DEEPGRAM_API_KEY', None):
        return res.json({
            "success": False,
            "message": "Please provide DEEPGRAM_API_KEY in variables"
        })

    api_key = req.variables.get('DEEPGRAM_API_KEY')

    # Setting up headers and body data for the requst
    headers = {
        'content-type': "application/json",
        'Authorization': f"Token {api_key}"
    }

    data = "{\"url\": \"" + url + "\"}"

    # Post requst to the Deepgram API
    response = requests.post(
        "https://api.deepgram.com/v1/listen", data=data, headers=headers
    )

    # Responses for different status codes
    if response.status_code == 200:
        response = response.json()
        return res.json({
            "success": True,
            "deepgramData": response
        })

    elif response.status_code == 401:
        return res.json({
            "success": False,
            "message": "Please provide a valid DEEPGRAM_API_KEY"
        })

    elif response.status_code == 400:
        return res.json({
            "success": False,
            "message": "Please provide a valid audio URL"
        })

    else:
        try:
            response = response.json()
            return res.json({
                "success": False,
                "message": f"{response['error']}, {response['reason']}"
            })

        except JSONDecodeError:
            return res.json({
                "success": False,
                "message": response.text
            })
