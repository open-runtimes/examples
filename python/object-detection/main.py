import json
import requests

from cloudmersive_image_api_client import Configuration, RecognizeApi, ApiClient
from cloudmersive_image_api_client.rest import ApiException

def main(req, res):
    payload = json.loads(req.payload)
    file_url = payload.get("url", None)
    api_key = req.env.get("CLOUDMERSIVE_API_KEY", None)

    if file_url is None or api_key is None:
        return res.json({'error': 'Missing payload or env data.'})

    # Download the file to the container
    response = requests.get(file_url, stream=True)
    with open("temp.jpg", 'wb') as newFile:
        for chunk in response.iter_content(chunk_size=1024): 
            if chunk:
                newFile.write(chunk)
                newFile.flush()

    # Configure the detection client
    configuration = Configuration()
    configuration.api_key['Apikey'] = api_key

    # Create an instance of the API class
    api_client = RecognizeApi(ApiClient(configuration))

    # Detect objects including types and locations in an image
    api_response = api_client.recognize_detect_objects("temp.jpg")
    
    results = list(map(lambda x: {
        "name": x.object_class_name,
        "confidence": x.score,
        "x": x.x,
        "y": x.y,
        "width": x.width,
        "height": x.height
    }, api_response.objects))

    return res.json(results)
