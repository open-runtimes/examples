# Standard library
import base64
import unittest
import pathlib
from unittest.mock import patch

# Third party
import requests
from parameterized import parameterized
from google.cloud import texttospeech


# Local imports
import main
import secret

RESULT_GOOGLE = (
    pathlib.Path("python/text-to-speech/results/google.txt").
    read_text(encoding="utf-8"))

# Path to krakenio encoded result (str).
RESULT_AZURE = (
    pathlib.Path("python/text-to-speech/results/azure.txt").
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
    def test_validate_request(self, req):
        """Test validate_request method when all required fields are present."""
        pass

    def test_validate_request_missing_aws_access_key_id(self, req):
        """Test validate_request method when 'AWS_ACCESS_KEY_ID' is missing."""
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


class ValidateCommonTest(unittest.TestCase):
    """Test Cases for validate_common function"""
    def test_validate_common(self, req):
        """Test validate_common function with valid input."""
        pass

    def test_missing_text(self, req):
        """Test validate_common function when 'text' is missing."""
        pass

    def test_missing_language(self, req):
        """Test validate_common function when 'language' is missing."""
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
    def test_validate_request(self, req):
        """Test validate_request method when all required fields are present."""
        pass

    def test_validate_request_missing_aws_access_key_id(self, req):
        """Test validate_request method when 'AWS_ACCESS_KEY_ID' is missing."""
        pass

    def test_validate_request_missing_aws_secret_access_key(self, req):
        """Test validate_request method when 'AWS_SECRET_ACCESS_KEY' is missing."""
        pass

    def test_speech(self):
        """Test speech method for successful text-to-speech synthesis."""
        req = MyRequest({
            "payload": {
                "provider": "aws",
                "text": "hi",
                "language": "en-US",
            },
            "variables": {
                "API_KEY": "123",
                "PROJECT_ID": "123",
            }
        })
        # Create an instance of Google Class
        aws_instance = main.AWS(req)
        # Variables
        text = "hello"
        language = "en-US"
        # Set up mock
        with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
            mock_response = mock_client.return_value
            mock_response.synthesize_speech.return_value.audio_content = base64.b64decode(RESULT_AWS)

            # Call the speech method
            audio_stream = aws_instance.speech(text, language)

            # Assert that the mock client was called with the correct arguments
            mock_client.assert_called_once_with(client_options={"api_key": "123", "secret_api_key": "123"})

            # Assert that the synthesize_speech method was called with the correct arguments
            mock_response.synthesize_speech.assert_called_once_with(VoiceId="Joanna", OutputFormat="mp3", Text=text, LanguageCode=language)

            # Assert the result
            self.assertEqual(audio_stream, base64.b64decode(RESULT_AWS))

    def test_speech_key_exception(self, text, language):
        """Test speech method for handling exceptions during text-to-speech synthesis."""
        pass


class ValidateCommonTest(unittest.TestCase):
    """Test Cases for validate_common function"""
    def test_validate_common(self, req):
        """Test validate_common function with valid input."""
        pass

    def test_missing_text(self, req):
        """Test validate_common function when 'text' is missing."""
        pass

    def test_missing_language(self, req):
        """Test validate_common function when 'language' is missing."""
        pass


if __name__ == "__main__":
    unittest.main()
