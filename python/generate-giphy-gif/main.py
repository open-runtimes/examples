import json
import requests

def main(req, res):
    # Input validation
    search = None
    try:
        payload = json.loads(req.payload)
        search = payload['search']
    except Exception as err:
        print(err)
        raise Exception('Payload is invalid.')

    if not search:
        raise Exception('Invalid search.')

    # Make sure we have envirnment variables required to execute
    if not req.variables.get('GIPHY_API_KEY', None):
        raise Exception('Please provide all required environment variables.')

    api_key = req.variables.get('GIPHY_API_KEY', None)

    # Fetch the Giphy URL
    response = requests.get(f"https://api.giphy.com/v1/gifs/search?api_key={api_key}&q={search}&limit=1")
    response.raise_for_status()
    url = response.json()["data"][0]['url']

    # Return Giphy URL
    return res.json({'search': search, 'gif': url})