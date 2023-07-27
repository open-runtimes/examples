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
    def setUp(self):
        self.req = MyRequest({
                    "payload": {
                        "provider": "google",
                        "text": "hi",
                        "language": "en-US",
                    },
                    "variables": {
                        "API_KEY": secret.GOOGLE_API_KEY,
                        "PROJECT_ID": secret.GOOGLE_PROJECT_ID,
                    }
        })
        self.google_instance = main.Google(self.req)

    def tearDown(self):
        self.google_instance = None

    @parameterized.expand([
        ({"API_KEY": None, "PROJECT_ID": "123"},),  # Missing API KEY
        ({"API_KEY": "123", "PROJECT_ID": None},),  # Missing PROJECT ID
        ({"API_KEY": None, "PROJECT_ID": None},),  # Missing Both
    ])
    def test_validate_request(self, variables):
        self.req.variables["API_KEY"] = variables["API_KEY"]
        self.req.variables["PROJECT_ID"] = variables["PROJECT_ID"]
        self.assertRaises(ValueError, self.google_instance.validate_request, self.req)

    def test_speech_happy(self):
        """Test speech method for successful text-to-speech synthesis."""
        self.google_instance.api_key = "123"
        self.google_instance.project_id = "123"
        # Set up mock
        with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
            mock_response = mock_client.return_value
            mock_response.synthesize_speech.return_value.audio_content = base64.b64decode(RESULT_GOOGLE)
            # Call the speech method
            audio_stream = self.google_instance.speech("hi", "en-US")
            # Assert the result
            self.assertEqual(audio_stream, base64.b64decode(RESULT_GOOGLE))

    def test_speech_error(self):
        """Test speech method for unsuccessful text-to-speech synthesis."""
        self.google_instance.api_key = "123"
        self.google_instance.project_id = "123"
        # Variables
        # Set up mock
        with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
            mock_response = mock_client.return_value
            mock_response.synthesize_speech.return_value.audio_content = b'A12'
            # Call the speech method
            audio_stream = self.google_instance.speech("hello", "en-US")
            # Assert the result
            self.assertNotEqual(audio_stream, base64.b64decode(RESULT_GOOGLE))

    # def test_google_credential(self):
    #     self.google_instance.api_key = "INCORRECT_CREDENTIAL"
    #     self.google_instance.project_id = "INCORRECT_CREDENTIAL"
    #     # Set up mock
    #     with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
    #         mock_client.side_effect = Exception
    #     self.assertRaises(Exception, self.google_instance.speech, "hello", "en-US")  # Incorrect credentials raise exception

    # def test_google_language(self):
    #     with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
    #         mock_client.side_effect = Exception
    #     self.assertRaises(Exception, self.google_instance.speech, "hello", "en-EN")  # Incorrect language
    #     self.assertRaises(Exception, self.google_instance.speech, "hello", None)  # Empty language code

    # def test_speech_text(self):
    #     with patch.object(texttospeech, "TextToSpeechClient") as mock_client:
    #         mock_client.side_effect = Exception
    #     self.assertRaises(Exception, self.google_instance.speech, None, "en-US")  # Empty Text

    # def test_google_credential(self):
    #     self.google_instance.api_key = "INCORRECT_CREDENTIAL"
    #     self.google_instance.project_id = "INCORRECT_CREDENTIAL"
    #     self.assertRaises(Exception, self.google_instance.speech, "hello", "en-US")  # Incorrect credentials raise exception

    # def test_google_language(self):
        # self.assertRaises(Exception, self.google_instance.speech, "hello", "en-EN")  # Incorrect language code
        # self.assertRaises(Exception, self.google_instance.speech, "hello", None)  # Empty language code

    # def test_speech_text(self):
        # self.assertRaises(Exception, self.google_instance.speech, None, "en-US")
        # self.assertEqual(b'', self.google_instance.speech("", "en-US"))


class AzureTest(unittest.TestCase):
    """Azure API Test Cases"""
    def setUp(self):
        self.req = MyRequest({
                    "payload": {
                        "provider": "azure",
                        "text": "hi",
                        "language": "en-US",
                    },
                    "variables": {
                        "API_KEY": secret.AZURE_API_KEY,
                        "REGION_KEY": secret.AZURE_REGION_KEY,
                    }
        })
        self.azure_instance = main.Azure(self.req)

    def tearDown(self):
        self.azure_instance = None

    # def test_validate_request(self, req):
        """Test validate_request method when all required fields are present."""
        # pass

#     def test_validate_request_missing_aws_access_key_id(self, req):
#         """Test validate_request methsod when 'AWS_ACCESS_KEY_ID' is missing."""
#         pass

#     def test_validate_request_missing_aws_secret_access_key(self, req):
#         """Test validate_request method when 'AWS_SECRET_ACCESS_KEY' is missing."""
#         pass

#     def test_speech(self, text, language):
#         """Test speech method for text-to-speech synthesis."""
#         pass

#     def test_speech_key_exception(self, text, language):
#         """Test speech method for handling exceptions during text-to-speech synthesis."""
#         pass


# class AWSTest(unittest.TestCase):
#     """AWS API Test Cases"""
#     def test_validate_request(self, req):
#         """Test validate_request method when all required fields are present."""
#         pass

#     def test_validate_request_missing_aws_access_key_id(self, req):
#         """Test validate_request method when 'AWS_ACCESS_KEY_ID' is missing."""
#         pass

#     def test_validate_request_missing_aws_secret_access_key(self, req):
#         """Test validate_request method when 'AWS_SECRET_ACCESS_KEY' is missing."""
#         pass

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

#     def test_speech_key_exception(self, text, language):
#         """Test speech method for handling exceptions during text-to-speech synthesis."""
#         pass


# class ValidateCommonTest(unittest.TestCase):
#     """Test Cases for validate_common function"""
#     def test_validate_common(self, req):
#         """Test validate_common function with valid input."""
#         pass

#     def test_missing_text(self, req):
#         """Test validate_common function when 'text' is missing."""
#         pass

#     def test_missing_language(self, req):
#         """Test validate_common function when 'language' is missing."""
#         pass


# class MainTest(unittest.TestCase):
#     """Test Cases for main function."""
#     @unittest.skipUnless(secret.GOOGLE_API_KEY, "No Google API Key set.")
#     def test_main(self):
#         """Unittest for main function success json response."""
#         want = {
#             "success": True,
#             "audio_stream": RESULT_GOOGLE,
#         }
#         # Create a request
#         req = MyRequest({
#             "payload": {
#                 "provider": "google",
#                 "text": "hi",
#                 "language": "en-US",
#             },
#             "variables": {
#                 "API_KEY": "123",
#                 "PROJECT_ID": "123",
#             }
#         })
#         res = MyResponse()
#         main.main(req, res)

#         # Check the response
#         got = res.json()
#         self.assertEqual(got, want)

#     def test_main_value_error(self):
#         """Unittest for main function when a value error is raised."""
#         want = {"success": False, "error": "Missing payload"}
#         # Create a request
#         req = MyRequest({"payload": {}, "variables": {}})
#         # Create a response object
#         res = MyResponse()
#         main.main(req, res)

#         # Check the response
#         got = res.json()
#         self.assertEqual(got, want)

#     def test_main_exception(self):
#         """Unittest case for main function when exception is raised."""
#         # Create a request
#         req = MyRequest({
#             "payload": {
#                 "provider": "tinypng",
#                 "image": base64.b64encode(IMAGE).decode()
#             },
#             "variables": {
#                 "API_KEY": "wrong_api_key"
#             }
#         })
#         # Create a response object
#         res = MyResponse()
#         main.main(req, res)

#         # Check the response
#         got = res.json()
#         self.assertFalse(got["success"])
#         self.assertIn("AccountError", got["error"])

if __name__ == "__main__":
    unittest.main()
