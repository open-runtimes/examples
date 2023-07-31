# Standard library
import base64
import unittest
import pathlib
from unittest.mock import patch

# Third party
import requests
from parameterized import parameterized
from google.cloud import texttospeech
import boto3

# Local imports
import main

RESULT_GOOGLE = (
    pathlib.Path("results/google.txt").
    read_text(encoding="utf-8"))

# Path to krakenio encoded result (str).
RESULT_AZURE = (
    pathlib.Path("results/azure.txt").
    read_text(encoding="utf-8"))

RESULT_AWS = (
    pathlib.Path("results/aws.txt").
    read_text(encoding="utf-8"))

def get_instance(provider, key, project_id):
    IMPLEMENTATIONS = {
        "google": main.Google,
        "azure": main.Azure,
        "aws": main.AWS,
    }

    req = MyRequest({
        "payload": {
            "provider": provider,
            "text": "hi",
            "language": "en-US",
        },
        "variables": {
            "API_KEY": key,
            "PROJECT_ID": project_id,
        }
    })

    return IMPLEMENTATIONS[provider](req)


class MyRequest:
    """Class for defining My Request structure."""
    def __init__(self, data):
        self.payload = data.get("payload", {})
        self.variables = data.get("variables", {})


class MyResponse:
    """Class for defining My Response structure."""
    def __init__(self):
        self._json = None

    def json(self, data=None):
        """Create a response for json."""
        if data is not None:
            self._json = data
        return self._json


class GoogleTest(unittest.TestCase):
    """Google API Test Cases"""
    @parameterized.expand([
        (None, "123"),  # Missing API KEY
        ("123", None),  # Missing PROJECT ID
        (None, None),  # Missing Both
    ])
    def test_validate_request(self, key, project_id):
        self.assertRaises(ValueError, get_instance, "google", key, project_id)

    def test_speech_happy(self):
        """Test speech method for successful text-to-speech synthesis."""
        instance = get_instance("google", "123", "123")
        # Set up mock
        with patch.object(texttospeech.TextToSpeechClient, "synthesize_speech") as mock_synthesize_speech:
            mock_synthesize_speech.return_value.audio_content = base64.b64decode(RESULT_GOOGLE)
            # Call the speech method
            audio_bytes = instance.speech("hi", "en-US")
            # Assert the result
            self.assertEqual(audio_bytes, base64.b64decode(RESULT_GOOGLE))

    def test_speech_error(self):
        """Test speech method for unsuccessful text-to-speech synthesis."""
        instance = get_instance("google", "123", "123")
        # Set up mock
        with patch.object(texttospeech.TextToSpeechClient, "synthesize_speech") as mock_synthesize_speech:
            mock_synthesize_speech.return_value.audio_content = b"INCORRECT_VALUE"
            # Call the speech method
            audio_bytes = instance.speech("hi", "en-US")
            # Assert the result
        self.assertNotEqual(audio_bytes, base64.b64decode(RESULT_GOOGLE))

    def test_google_credential(self):
        instance = get_instance("google", "WRONG_API_KEY", "WRONG_PROJECT_ID")
        # Set up mock
        with patch.object(texttospeech.TextToSpeechClient, "synthesize_speech") as mock_synthesize_speech:
            # Raise Exception
            mock_synthesize_speech.side_effect = Exception
        self.assertRaises(Exception, instance.speech, "hello", "en-US")  # Incorrect credentials raise exception

    def test_google_language(self):
        instance = get_instance("google", "<YOUR_API_KEY>", "<YOUR_PROJECT_ID>")
        with patch.object(texttospeech.TextToSpeechClient, "synthesize_speech") as mock_synthesize_speech:
            mock_synthesize_speech.side_effect = Exception
        self.assertRaises(Exception, instance.speech, "hello", "en-EN")  # Incorrect language
        self.assertRaises(Exception, instance.speech, "hello", None)  # Empty language code

    def test_speech_text(self):
        instance = get_instance("google", "<YOUR_API_KEY>", "<YOUR_PROJECT_ID>")
        with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
            mock_client.side_effect = Exception
        self.assertRaises(Exception, instance.speech, None, "en-US")  # Empty Text


class AzureTest(unittest.TestCase):
    """Azure API Test Cases"""

    def test_validate_request(self, req):
        """Test validate_request method when all required fields are present."""
        pass

    def test_validate_request_missing_aws_access_key_id(self, req):
        """Test validate_request methsod when 'AWS_ACCESS_KEY_ID' is missing."""
        pass

    def test_validate_request_missing_aws_secret_access_key(self, req):
        """Test validate_request method when 'AWS_SECRET_ACCESS_KEY' is missing."""
        pass

    def test_speech(self, text, language):
        """Test speech method for text-to-speech synthesis."""
        pass

    def test_speech_key_exception(self, text, language):
        """Test speech method for handling exceptions during text-to-speech synthesis."""
        pass


class AWSTest(unittest.TestCase):
    """AWS API Test Cases"""
    def get_instance(self, key, secret_key):
        req = MyRequest({
            "payload": {
                "provider": "aws",
                "text": "hi",
                "language": "en-US",
            },
            "variables": {
                "API_KEY": key,
                "SECRET_API_KEY": secret_key,
            }
        })
        return main.AWS(req)

    @parameterized.expand([
        (None, "123"),  # Missing API KEY
        ("123", None),  # Missing SECRET API KEY
        (None, None),  # Missing Both
    ])
    def test_validate_request(self, key, secret_key):
        self.assertRaises(ValueError, self.get_instance, key, secret_key)

    def test_speech_happy(self):
        """Test speech method for successful text-to-speech synthesis."""
        instance = self.get_instance("123", "123")
        # Set up mock
        with patch.object(boto3.Session, "client") as mock_client:
            mock_response = {"Audiostream": base64.b64decode(RESULT_AWS)}
            mock_client.return_value.synthesize_speech.return_value = mock_response
            got = instance.speech("hi", "en-US")
            want = base64.b64decode(RESULT_AWS)
            # Assert the result
            self.assertEqual(got, want)

    def test_speech_key_exception(self):
        """Test speech method for handling exceptions during text-to-speech synthesis."""
        instance = self.get_instance("123", "123")
        self.assertRaises(Exception, instance.speech, "hi", "en-US")


class ValidateCommonTest(unittest.TestCase):
    """Test Cases for validate_common function"""
    def get_req(self, payload, variables, provider, text, language):
        return MyRequest({
            payload: {
                "provider": provider,
                "text": text,
                "language": language,
            },
            variables: {
                "key": "123",
            }
        })

    def test_validate_common_happy(self):
        """Test validate common method happy path."""
        want = ("google", "hi", "en-US")
        req = self.get_req("payload", "variables", "google", "hi", "en-US")
        got = main.validate_common(req)
        self.assertEquals(got, want)

    @parameterized.expand([
        ("", "variables", "aws", "hi", "en-US"),  # Missing payload
        ("payload", "", "aws", "hi", "en-US"),  # Missing variables
        ("payload", "variables", "", "hi", "en-US"),  # Missing provider
        ("payload", "variables", "awss", "hi", "en-US"),  # Invalid provider
        ("payload", "variables", "aws", "", "en-US"),  # Missing text
        ("payload", "variables", "aws", "hi", ""),  # Missing language
    ])
    def test_validate_common_errors(self, payload, variables, provider, text, language):
        """Test validate common method when it raises value errors."""
        req = self.get_req(payload, variables, provider, text, language)
        self.assertRaises(ValueError, main.validate_common, req)


if __name__ == "__main__":
    unittest.main()
