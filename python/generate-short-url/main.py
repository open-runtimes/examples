import json
import requests


def main(req, res):
    # Required parameters
    provider = None
    url = None

    try:
        payload = json.loads(req.payload)
        provider = payload['provider']
        url = payload['url']
        
    except Exception:
        err = 'Please provide all the required parameters.'
        return res.json({
            "success": False,
            "message": err,
        }) 

    # Logic if the provider is tinyurl
    if provider == "tinyurl":
        api_key = req.env.get('TINYURL_API_KEY', None)
        response = requests.post(f'https://api.tinyurl.com/create?api_token={api_key}&url={url}')
        response = response.json()

        if response['code'] == 0:
            return res.json({
                "success": True,
                "url": response['data']['tiny_url']
            })
        
        elif response['code'] == 1:
            return res.json({
                "success": False,
                "message": "Please provide a valid API Key"
            })
            
        elif response['code'] == 5:
            return res.json({
                "success": False,
                "message": "Please provide a valid URL"
            })
            
        else:
            return res.json({
                "success": False,
                "message": response['errors']
            })

    # Logic if provider is bitly
    elif provider == "bitly":
        api_key = req.env.get('BITLY_API_KEY', None)
        headers = {
            'Authorization': f'Bearer {api_key}',
            'Content-Type': 'application/json',
        }
        data = '{"long_url": "' + url + '"}'

        response = requests.post('https://api-ssl.bitly.com/v4/shorten', headers=headers, data=data)
        
        if response.status_code == 200:
            response = response.json()
            return res.json({
                "success": True,
                "url": response['link']
            })
        elif response.status_code == 403:
            response = response.json()
            return res.json({
                "success": False,
                "message": "Please provide a valid API Key"
            })
            
        else:
            response = response.json()
            return res.json({
                "success": False,
                "message": str(response['message'])
            })

    # Logic if the Provider is not valid.
    else:
        err = f'{provider} is not a valid provider.'
        return res.json({
            "success": False,
            "message": err
        }) 
