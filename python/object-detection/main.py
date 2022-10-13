import json
import requests

from cloudmersive_image_api_client import Configuration, RecognizeApi, ApiClient
from cloudmersive_image_api_client.rest import ApiException

def main(req, res):
    # Input validation
    file_url = None
    try:
        payload = json.loads(req.payload)
        file_url = payload['url']
    except Exception as err:
        print(err)
        raise Exception('Payload is invalid.')

    if not file_url:
        raise Exception('Invalid search.')
    
    # Make sure we have envirnment variables required to execute
    if not req.variables.get('CLOUDMERSIVE_API_KEY', None):
        raise Exception('Please provide all required environment variables.')

    api_key = req.variables.get('CLOUDMERSIVE_API_KEY', None)

    # Download the file
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
    
    # Return object detection results
    results = list(map(lambda x: {
        "name": x.object_class_name,
        "confidence": x.score,
        "x": x.x,
        "y": x.y,
        "width": x.width,
        "height": x.height
    }, api_response.objects))

    if len(results) < 1:
        raise Exception('No objects found in the image.')

    return res.json(results[0])
