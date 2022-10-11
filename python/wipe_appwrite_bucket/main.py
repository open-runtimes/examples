
import json
from appwrite.client import Client
from appwrite.services.storage import Storage


def main(req, res):
    client = Client()
    

    print("Starting function")

    # Make sure we have envirnment variables required to execute
    if not req.variables.get('APPWRITE_FUNCTION_ENDPOINT', None) or not req.variables.get('APPWRITE_FUNCTION_API_KEY', None):
        return res.json({"success":False,"message":"Required environment variables not found"})
    
    payload = json.loads(req.payload)

    if 'bucketId' not in payload:
        return res.json({"success":False,"message":"Missing bucketId"})

    bucket_id = payload['bucketId']

    (client
        .set_endpoint(req.variables.get('APPWRITE_FUNCTION_ENDPOINT', None))
        .set_project(req.variables.get('APPWRITE_FUNCTION_PROJECT_ID', None))
        .set_key(req.variables.get('APPWRITE_FUNCTION_API_KEY', None))
    )
    storage = Storage(client)

    # Get all files in the bucket
    try:
        files = storage.list_files(bucket_id)
    except:
        return res.json({"success":False,"message":"Bucket not found."})

    # Delete all files in the bucket
    for file in files['files']:
        storage.delete_file(bucket_id,file['$id'])

    return res.json({"success":True})