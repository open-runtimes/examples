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
    def get_google_instance(self, key, project_id):
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
        (None, "123"),  # Missing API KEY
        ("123", None),  # Missing PROJECT ID
        (None, None),  # Missing Both
    ])
    def test_validate_request(self, key, project_id):
        self.assertRaises(ValueError, self.get_google_instance, key, project_id)

    def test_speech_happy(self):
        """Test speech method for successful text-to-speech synthesis."""
        instance = self.get_google_instance("123", "123")
        # Set up mock
        with patch.object(texttospeech.TextToSpeechClient, "synthesize_speech") as mock_synthesize_speech:
            mock_synthesize_speech.return_value.audio_content = base64.b64decode(RESULT_GOOGLE)
            # Call the speech method
            audio_bytes = instance.speech("hi", "en-US")
            # Assert the result
            self.assertEqual(audio_bytes, base64.b64decode(RESULT_GOOGLE))

    def test_speech_error(self):
        """Test speech method for unsuccessful text-to-speech synthesis."""
        instance = self.get_google_instance("123", "123")
        # Set up mock
        with patch.object(texttospeech.TextToSpeechClient, "synthesize_speech") as mock_synthesize_speech:
            mock_synthesize_speech.return_value.audio_content = b"INCORRECT_VALUE"
            # Call the speech method
            audio_bytes = instance.speech("hi", "en-US")
            # Assert the result
        self.assertNotEqual(audio_bytes, base64.b64decode(RESULT_GOOGLE))

    def test_google_credential(self):
        instance = self.get_google_instance("WRONG_API_KEY", "WRONG_PROJECT_ID")
        # Set up mock
        with patch.object(texttospeech.TextToSpeechClient, "synthesize_speech") as mock_synthesize_speech:
            # Raise Exception
            mock_synthesize_speech.side_effect = Exception
        # Incorrect credentials raise exception
        self.assertRaises(Exception, instance.speech, "hello", "en-US")

    def test_google_language(self):
        instance = self.get_google_instance("<YOUR_API_KEY>", "<YOUR_PROJECT_ID>")
        with patch.object(texttospeech.TextToSpeechClient, "synthesize_speech") as mock_synthesize_speech:
            mock_synthesize_speech.side_effect = Exception
        # Incorrect language
        self.assertRaises(Exception, instance.speech, "hello", "en-EN")
        # Empty language code
        self.assertRaises(Exception, instance.speech, "hello", None)

    def test_speech_text(self):
        instance = self.get_google_instance("<YOUR_API_KEY>", "<YOUR_PROJECT_ID>")
        with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
            mock_client.side_effect = Exception
        # Set empty text
        self.assertRaises(Exception, instance.speech, None, "en-US")


# class AzureTest(unittest.TestCase):
#     """Azure API Test Cases"""
#     def get_azure_instance(self, key, project_id):
#         req = MyRequest({
#             "payload": {
#                 "provider": "azure",
#                 "text": "hi",
#                 "language": "en-US",
#             },
#             "variables": {
#                 "API_KEY": key,
#                 "PROJECT_ID": project_id,
#             }
#         })
#         return main.Azure(req)
    
#     @parameterized.expand([
#         (None, "123"),  # Missing API KEY
#         ("123", None),  # Missing PROJECT ID
#         (None, None),  # Missing Both
#     ])
#     def test_validate_request(self, key, project_id):
#         """Test validate method when all required fields are present."""
#         self.assertRaises(ValueError, self.get_azure_instance, key, project_id)

#     def test_speech_happy(self):
#         """Test speech method for successful text-to-speech synthesis."""
#         instance = self.get_azure_instance("123", "123")
#         # Set up mock
#         with patch.object(speechsdk.SpeechSynthesizer, "speak_text_async") as mock_synthesize_speech:
#             mock_synthesize_speech.return_value.audio_content = base64.b64decode(RESULT_AZURE)
#             # Call the speech method
#             audio_bytes = instance.speech("hi", "en-US")
#             # Assert the result
#             self.assertEqual(audio_bytes, base64.b64decode(RESULT_AZURE))

#     def test_validate_request_missing_aws_secret_access_key(self, req):
#         """Test validate_request method when 'AWS_SECRET_ACCESS_KEY' is missing."""
#         pass

#     def test_speech(self, text, language):
#         """Test speech method for text-to-speech synthesis."""
#         pass

#     def test_speech_key_exception(self, text, language):
#         """Test speech method for handling exceptions during text-to-speech synthesis."""
#         pass


class AWSTest(unittest.TestCase):
    """AWS API Test Cases"""
    def get_aws_instance(self, key, secret_key):
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
        self.assertRaises(ValueError, self.get_aws_instance, key, secret_key)

    def test_speech_happy(self):
        """Test speech method for successful text-to-speech synthesis."""
        instance = self.get_aws_instance("123", "123")
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
        instance = self.get_aws_instance("123", "123")
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
        self.assertEqual(got, want)

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
