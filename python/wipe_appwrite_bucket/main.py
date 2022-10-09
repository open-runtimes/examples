
import json
from appwrite.client import Client
from appwrite.services.storage import Storage


def main(req, res):
    client = Client()
    storage = Storage(client)

    print("Starting function")

    # Make sure we have envirnment variables required to execute
    if not req.variables.get('APPWRITE_FUNCTION_ENDPOINT', None) or not req.variables.get('APPWRITE_FUNCTION_PROJECT_ID', None) or not req.variables.get('APPWRITE_FUNCTION_API_KEY', None):
        raise Exception('Please provide all required environment variables.')
    
    payload = json.loads(req.payload)
    
    if 'bucketId' not in payload:
        raise Exception('Missing bucketId')

    bucket_id = payload['bucketId']

    # (client
    #     .set_endpoint(req.variables.get('APPWRITE_FUNCTION_ENDPOINT', None))
    #     .set_project(req.variables.get('APPWRITE_FUNCTION_PROJECT_ID', None))
    #     .set_key(req.variables.get('APPWRITE_FUNCTION_API_KEY', None))
    # )

    # # Get all files in the bucket
    # files = storage.list_files(bucket_id)

    # # Delete all files in the bucket
    # for file in files['files']:
    #     storage.delete_file(file['$id'])

    return res.json({"success":True})