import requests
import json


def errorMessage(res, message):
    return res.json({
        "success": False,
        "message": message,
    })
    
    
def main(req,res):
    
    headers = None
    body = None
    try:
        payload = json.loads(req.payload)
        
        #url and method are required
        url = payload['url']
        method = payload['method']
        
        # headers and body are optional
        if 'headers' in payload.keys():
            headers = payload['headers']
        if 'body' in payload.keys():
            body = payload['body']

    except Exception:
        return errorMessage(res, 'Payload must contain url and method.')
    if url.strip() == "":
        return errorMessage(res, 'Payload url cannot be empty.')
    if method.strip() == "":
        return errorMessage(res, 'Payload method cannot be empty.')

    r = None
    try:
        r = requests.request(method, url, headers=headers, json=body, timeout=30)
    except Exception:
        return errorMessage(res, 'URL could not be reached.')
    
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
        return errorMessage(res, f"{status} status error. Nothing to retrieve. Check that URL path is correct.")