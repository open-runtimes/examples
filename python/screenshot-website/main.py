import base64
import json
from typing import Any, Literal, Optional, Tuple
import requests
from urllib.parse import urlencode

def validate_environment_variables(request_obj: Any):

    try:
        environment_variables = json.loads(request_obj.env)
    except Exception as err:
        raise ValueError('Unable to fetch `url` from the request payload.')

    if not environment_variables.get('API_FLASH_API_KEY', None):
        raise Exception('Please provide all required environment variables.')

    return environment_variables.get('API_FLASH_API_KEY')

def build_screenshot_url(request_obj: Any, access_key: str) -> str:
    
    try:
        payload = json.loads(request_obj.payload)
    except Exception as err:
        raise ValueError('Unable to fetch `url` from the request payload.')

    if not payload.get("url", None):
        raise Exception('Please provide a url to screenshot.')

    file_url = payload["url"]
    params = urlencode(dict(access_key=access_key, url=file_url))

    return f"https://api.apiflash.com/v1/urltoimage?{params}"

def get_screenshot_from_api_flash(request_url: str) -> Tuple[Optional[str], bool]:
    screenshot_request = requests.get(request_url)

    if screenshot_request.status_code != 200:
        return None, False

    return base64.b64encode(requests.get(request_url).content).decode('utf-8'), True

def main(request, response):
    
    api_key = validate_environment_variables(request)
    screenshot_url = build_screenshot_url(request, api_key)
    base64_screenshot, is_successful = get_screenshot_from_api_flash(screenshot_url)

    if is_successful:
        result = {
            "success": True,
            "screenshot": base64_screenshot
        }
    else:
        result = {
            "success": False,
            "message": "Website could not be reached"
        }

    return response.json(result)