
import json
from appwrite.client import Client
from appwrite.services.locale import Locale

"""
Globaly-scoped cache used by function. Example value:
[
    {
        "code": "+1",
        "countryCode": "US",
        "countryName": "United States"
    }
]
"""

def main(req, res):
    global phone_prefix_list
    phone_prefix_list = None

    # Input validation
    phone_number = None
    try:
        payload = json.loads(req.payload)
        phone_number = payload['phoneNumber'].replace(" ", "")
    except Exception as err:
        print(err)
        raise Exception('Payload is invalid.')

    if not phone_number or not phone_number.startswith('+'):
        raise Exception('Invalid phone number.')

    # Make sure we have envirnment variables required to execute
    if not req.env.get('APPWRITE_FUNCTION_ENDPOINT', None) or not req.env.get('APPWRITE_FUNCTION_PROJECT_ID', None) or not req.env.get('APPWRITE_FUNCTION_API_KEY', None):
        raise Exception('Please provide all required environment variables.')

    # If we don't have cached list of phone number prefixes (first execution only)
    if phone_prefix_list is None:
        # Init Appwrite SDK
        client = Client()
        locale = Locale(client)

        (client
            .set_endpoint(req.env.get('APPWRITE_FUNCTION_ENDPOINT', None))
            .set_project(req.env.get('APPWRITE_FUNCTION_PROJECT_ID', None))
            .set_key(req.env.get('APPWRITE_FUNCTION_API_KEY', None))
        )

        # Fetch and store phone number prefixes
        server_response = locale.get_countries_phones()
        phone_prefix_list = server_response['phones']

    # Get phone prefix
    phonePrefix = list(filter(lambda prefix: phone_number.startswith(prefix['code']), phone_prefix_list))

    if len(phonePrefix) < 1:
        raise Exception('Invalid phone number.')

    # Return phone number prefix
    return res.json({
        'phoneNumber': phone_number,
        'phonePrefix': phonePrefix[0]['code'],
        'countryCode': phonePrefix[0]['countryCode'],
        'countryName': phonePrefix[0]['countryName']
    })