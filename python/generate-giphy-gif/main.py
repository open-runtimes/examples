import json
import requests

def main(req, res):
    payload = json.loads(req.payload)
    search = payload.get('search', None)
    api_key = req.env.get('GIPHY_API_KEY', None)

    if search is None or api_key is None:
        return res.json({'error': 'Missing payload or env data.'})

    response = requests.get(f"https://api.giphy.com/v1/gifs/search?api_key={api_key}&q={search}&limit=1")
    response.raise_for_status()
    url = response.json()["data"][0]['url']
    
    return res.json({'url': url})
