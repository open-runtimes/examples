import requests
import json

def main(req,res):
    try:
        payload = json.loads(req.payload)
        url = payload['url']
        method = payload['method']
        headers = payload['headers']
        body = payload['body']

    except Exception as err:
        print(err)
        raise Exception('Please input a valid payload.')

    r = requests.request(method, url, headers=headers, json=body, timeout=30)
    # r.raise_for_status()
    
    status = r.status_code
    if status == requests.codes.ok:
        return res.json({
            "success": True,
            "response":
            {
                "headers": json.dumps(dict(r.headers)),
                "code": status,
                "body": r.json()
            }
        })
    else:
        return res.json({
            "success": False,
            "message": "URL could not be reached.",
        })