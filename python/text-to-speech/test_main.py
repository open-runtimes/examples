"""Unittests for Text To Speech Function"""
# Standard library
import base64
import unittest
import pathlib
from unittest.mock import patch
from unittest.mock import MagicMock
import io

# Third party
import requests
from parameterized import parameterized
from google.cloud import texttospeech
import boto3
import botocore.response

# Local imports
import main

# Path to Google encoded result (str).
RESULT_GOOGLE = (
    pathlib.Path("results/google.txt").
    read_text(encoding="utf-8")
)

# Path to Azure encoded result (str).
RESULT_AZURE = (
    pathlib.Path("results/azure.txt").
    read_text(encoding="utf-8")
)
# Path to Aws encoded result (str).
RESULT_AWS = (
    pathlib.Path("results/aws.txt").
    read_text(encoding="utf-8")
)

DECODED_RESULT_GOOGLE = base64.b64decode(RESULT_GOOGLE)
DECODED_RESULT_AZURE = base64.b64decode(RESULT_AZURE)
DECODED_RESULT_AWS = base64.b64decode(RESULT_AWS)


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
    """Google API Test Cases."""
    def get_google_instance(self, key, project_id):
        """Set Google instance with request."""
        req = MyRequest({
            "payload": {
                "provider": "google",
                "text": "hi",
                "language": "en-US",
            },
            "variables": {
                "API_KEY": key,
                "PROJECT_ID": project_id,
            }
        })
        return main.Google(req)

    @parameterized.expand([
        # Missing API_KEY.
        (None, "123"),
        # Missing PROJECT_ID.
        ("123", None),
        # Missing Both.
        (None, None),
    ])
    def test_validate_request(self, key, project_id):
        """Test 'validate_request' method when fields missing or invalid."""
        self.assertRaises(
            ValueError,
            self.get_google_instance,
            key,
            project_id,
        )

    def test_speech_happy(self):
        """Test speech method for successful text-to-speech synthesis."""
        instance = self.get_google_instance("123", "123")
        # Set up mock
        patched_obj = texttospeech.TextToSpeechClient
        patched_func = "synthesize_speech"
        with patch.object(patched_obj, patched_func) as mock_synthesize_speech:
            mock_synthesize_speech.return_value.audio_content = (
                DECODED_RESULT_GOOGLE
            )
            # Call the speech method
            audio_bytes = instance.speech("hi", "en-US")
            # Assert the result
            self.assertEqual(audio_bytes, DECODED_RESULT_GOOGLE)

    def test_speech_error(self):
        """Test speech method for unsuccessful text-to-speech synthesis."""
        instance = self.get_google_instance("123", "123")
        # Set up mock.
        patched_obj = texttospeech.TextToSpeechClient
        patched_func = "synthesize_speech"
        with patch.object(patched_obj, patched_func) as mock_synthesize_speech:
            mock_synthesize_speech.return_value.audio_content = (
                b"INCORRECT_VALUE"
            )
            # Call the speech method.
            audio_bytes = instance.speech("hi", "en-US")
            # Assert the result.
        self.assertNotEqual(audio_bytes, DECODED_RESULT_GOOGLE)

    def test_google_credential(self):
        """Test credentials for speech method."""
        instance = self.get_google_instance(
            "WRONG_API_KEY",
            "WRONG_PROJECT_ID",
        )
        # Set up mock.
        patched_obj = texttospeech.TextToSpeechClient
        patched_func = "synthesize_speech"
        with patch.object(patched_obj, patched_func) as mock_synthesize_speech:
            # Raise Exception.
            mock_synthesize_speech.side_effect = Exception
        # Assert the raise.
        self.assertRaises(Exception, instance.speech, "hello", "en-US")

    def test_google_language(self):
        """Test language for speech method."""
        instance = self.get_google_instance(
            "<YOUR_API_KEY>",
            "<YOUR_PROJECT_ID>",
        )
        # Set up mock.
        patched_obj = texttospeech.TextToSpeechClient
        patched_func = "synthesize_speech"
        with patch.object(patched_obj, patched_func) as mock_synthesize_speech:
            # Raise Exception.
            mock_synthesize_speech.side_effect = Exception
        # Assert the raise.
        self.assertRaises(Exception, instance.speech, "hello", "en-EN")
        self.assertRaises(Exception, instance.speech, "hello", None)

    def test_speech_text(self):
        """Test text-content for speech method."""
        instance = self.get_google_instance(
            "<YOUR_API_KEY>",
            "<YOUR_PROJECT_ID>",
        )
        # Set mock.
        with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
            # Raise Exception.
            mock_client.side_effect = Exception
        # Assert the raise.
        self.assertRaises(Exception, instance.speech, None, "en-US")


class AzureTest(unittest.TestCase):
    """Azure API Test Cases."""
    def get_azure_instance(self, key, project_id):
        """Set Azure instance with request."""
        req = MyRequest({
            "payload": {
                "provider": "azure",
                "text": "hi",
                "language": "en-US",
            },
            "variables": {
                "API_KEY": key,
                "REGION_KEY": project_id,
            }
        })
        return main.Azure(req)

    @parameterized.expand([
        # Missing API_KEY.
        (None, "123"),
        # Missing REGION_KEY.
        ("123", None),
        # Missing Both.
        (None, None),
    ])
    def test_validate_request(self, key, project_id):
        """Test 'validate_request' method when fields missing or invalid."""
        self.assertRaises(ValueError, self.get_azure_instance, key, project_id)

    def test_speech_happy(self):
        """Test speech method for successful text-to-speech synthesis."""
        instance = self.get_azure_instance("123", "123")
        # Mock the requests.post method used in get_token.
        with patch("requests.post") as mock_post:
            mock_response = MagicMock()
            mock_response.text = "fake_access_token"
            mock_post.return_value = mock_response
            # Mock the requests.request method.
            with patch("requests.request") as mock_request:
                mock_response_request = MagicMock()
                mock_response_request.content = DECODED_RESULT_AZURE
                mock_request.return_value = mock_response_request
                # Call the speech method.
                audio_bytes = instance.speech("hi", "en-US")
                # Assert the result.
                self.assertEqual(audio_bytes, DECODED_RESULT_AZURE)

    def test_credential(self):
        """Test credentials for speech method."""
        instance = self.get_azure_instance("WRONG_API_KEY", "WRONG_PROJECT_ID")
        # Mock the requests.post method used in get_token.
        with patch("requests.post") as mock_post:
            mock_response = requests.Response
            mock_response.text = "fake_access_token"
            mock_post.return_value = mock_response
            # Mock the requests.request method.
            with patch("requests.request") as mock_request:
                mock_request.return_value = Exception("Some error occurred")
                # Assert the raise.
                self.assertRaises(Exception, instance.speech, "hi", "en-US")


class AWSTest(unittest.TestCase):
    """AWS API Test Cases."""
    def get_aws_instance(self, key, secret_key):
        """Set AWS instance with request."""
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
        # Missing API_KEY.
        (None, "123"),
        # Missing SECRET_API_KEY.
        ("123", None),
        # Missing Both.
        (None, None),
    ])
    def test_validate_request(self, key, secret_key):
        """Test 'validate_request' method when fields missing or invalid."""
        self.assertRaises(ValueError, self.get_aws_instance, key, secret_key)

    def test_speech_happy(self):
        """Test speech method for successful text-to-speech synthesis."""
        instance = self.get_aws_instance("123", "123")

        # Set up mock.
        raw_stream = io.BytesIO(DECODED_RESULT_AWS)
        size = len(DECODED_RESULT_AWS)
        stream_body_obj = botocore.response.StreamingBody(raw_stream, size)
        with patch.object(boto3.Session, "client") as mock_client:
            mock_client.return_value.synthesize_speech.return_value = {
                    "AudioStream": stream_body_obj,
                    "ContentType": "bytes",
                    "RequestCharacters": 123,
            }
            got = instance.speech("hi", "en-US")
            want = DECODED_RESULT_AWS
            # Assert the result.
            self.assertEqual(got, want)

    def test_speech_key_exception(self):
        """Test speech method for exceptions during TTS synthesis."""
        instance = self.get_aws_instance("123", "123")
        # Assert the raise.
        self.assertRaises(Exception, instance.speech, "hi", "en-US")


class ValidateCommonTest(unittest.TestCase):
    """Test Cases for validate_common function"""
    def get_req(self, payload, variables, provider, text, language):
        """Get the request."""
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
        self.assertEqual(got, want)

    @parameterized.expand([
        # Missing payload.
        ("", "variables", "aws", "hi", "en-US"),
        # Missing variables.
        ("payload", "", "aws", "hi", "en-US"),
        # Missing provider.
        ("payload", "variables", "", "hi", "en-US"),
        # Invalid provider.
        ("payload", "variables", "awss", "hi", "en-US"),
        # Missing text.
        ("payload", "variables", "aws", "", "en-US"),
        # Missing language.
        ("payload", "variables", "aws", "hi", ""),
    ])
    def test_validate_common_errors(self, payload, var, provider, text, lang):
        """Test validate common method when it raises value errors."""
        req = self.get_req(payload, var, provider, text, lang)
        # Assert the raise.
        self.assertRaises(ValueError, main.validate_common, req)


if __name__ == "__main__":
    unittest.main()
