""" Compress image function using Tinypng and Krakenio API."""
import base64
import json
import tinify
import requests

KRAKEN_API_ENDPOINT = "https://api.kraken.io/v1/upload"
KRAKEN_USER_AGENT = (
    "Mozilla/5.0 (Windows NT 6.1; Win64; x64)AppleWebKit/"
    "537.36(KHTML, like Gecko)Chrome/40.0.2214.85 Safari/537.36"
)


def krakenio_impl(variables):
    """
    Implements image optimization using the Kraken.io API.

    Input:
        variables (dict): A dictionary containing the
        required variables for optimization.
    Returns:
        optimized_image (bytes): decoded optimized image.
    Raises:
        raise_for_status (method): raise an HTTPError if the HTTP request
        returned an unsuccessful status code.
    """
    # Headers for post request
    headers = {"User-Agent": KRAKEN_USER_AGENT}
    # Image that we will pass in
    files = {"file": variables["decoded_image"]}
    # Parameters for post request
    params = {
        "auth": {
            "api_key": variables["api_key"],
            "api_secret": variables["api_secret_key"]
        },
        "wait": True,  # Optional: Wait for the optimization to complete
        "dev": False,  # Optional: Set to false to enter user mode.
    }
    response = requests.post(
        url=KRAKEN_API_ENDPOINT,
        headers=headers,
        files=files,
        data={"data": json.dumps(params)},
        timeout=10,
    )
    # Check status code of response
    response.raise_for_status()
    data = response.json()
    # Response unsuccessful, raise error
    if not data["success"]:
        raise ValueError("KrakenIO was not able to compress image.")
    # Response successful, parse the response
    optimized_url = data["kraked_url"]
    optimized_image = requests.get(optimized_url, timeout=10).content
    return optimized_image


def tinypng_impl(variables):
    """
    Implements image optimization using the Tinypng API.

    Input:
        variables (dict): A dictionary containing the required variables
        for optimization. Includes api_key and decoded_image.
    Returns:
        tinify.from_buffer().tobuffer() (bytes): decoded optimized image.
    Raises:
        tinify.error (method): raised if tinify fails to compress image.
    """
    tinify.key = variables["api_key"]
    return tinify.from_buffer(variables["decoded_image"]).to_buffer()


def validate_request(req):
    """
    Validates the request and extracts the necessary information.

    Input:
        req (json): The request object containing the payload and variables.
    Returns:
        result (dict): Contains the validated request information.
    Raises:
        ValueError: If any required value is missing or invalid.
    """
    # Check if payload is empty
    if not req.payload:
        raise ValueError("Missing payload")
    # Accessing provider from payload
    if not req.payload.get("provider"):
        raise ValueError("Missing provider")
    # Check if payload is not empty
    if not req.variables:
        raise ValueError("Missing variables.")
    # Accessing api_key from variables
    if not req.variables.get("API_KEY"):
        raise ValueError("Missing API_KEY")
    # Accessing encoded image from payload
    if not req.payload.get("image"):
        raise ValueError("Missing encoding image")
    result = {
        "provider": req.payload.get("provider").lower(),
        "api_key": req.variables.get("API_KEY"),
        "decoded_image": base64.b64decode(req.payload.get("image")),
    }
    # Get secret key
    if req.payload.get("provider") == "krakenio":
        if not req.variables.get("SECRET_API_KEY"):
            raise ValueError("Missing api secret key.")
        result["api_secret_key"] = req.variables.get("SECRET_API_KEY")
    return result


IMPLEMENTATIONS = {
        "krakenio": krakenio_impl,
        "tinypng": tinypng_impl,
    }


def main(req, res):
    """
    The main function that runs validate_request and calls IMPLEMENTATIONS.

    Input:
        req (json): The request object.
        res (json): The response object.

    Returns:
        res (json): A JSON response containing the optimization results.
    """
    try:
        variables = validate_request(req)
    except (ValueError) as value_error:
        return res.json({
            "success": False,
            "error": f"{value_error}",
        })
    try:
        optimized_image = IMPLEMENTATIONS[variables["provider"]](variables)
    except Exception as error:
        return res.json({
            "success": False,
            "error": f"{type(error).__name__}: {error}",
        })
    return res.json({
        "success": True,
        "image": base64.b64encode(optimized_image).decode(),
    })
