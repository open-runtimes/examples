import base64
import tinify
import json
import os
import tempfile



def decode(encoded_value):
    decoded = base64.b64decode(encoded_value)
    return decoded

def encode(plaintext):
    encoded = base64.b64encode(plaintext)
    return encoded

def errorMessage(res, message):
  return res.json({
      "success": False,
      "message": "fail",
  })

def main(req, res):
    # Get the payload then grab the provider and image
    payload = req.payload
    provider = payload['provider']
    image = payload['image']

    # Get the variables (usually the api key)
    variables = req.variables

    # Grab the api key
    secret_key = variables['TINYPNG_API_KEY']
    # Set the tinify key as the api key
    tinify.key = secret_key

    # Decode the image data from base64
    decoded = base64.b64decode(image)

    return None