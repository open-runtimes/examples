"""Unittests for Text To Speech Function."""
# Standard library
import base64
import io
import pathlib
import unittest
from unittest.mock import MagicMock

# Third party
import boto3
import botocore.response
from google.cloud import texttospeech
from parameterized import parameterized
import requests

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

# Content of Google result (decoded bytes).
DECODED_RESULT_GOOGLE = base64.b64decode(RESULT_GOOGLE)
# Content of Azure result (decoded bytes).
DECODED_RESULT_AZURE = base64.b64decode(RESULT_AZURE)
# Content of AWS result (decoded bytes).
DECODED_RESULT_AWS = base64.b64decode(RESULT_AWS)


class MyRequest:
    """Class for defining My Request structure."""

    def __init__(
            self,
            data: dict[dict[str, str, str], dict[str, str]],
            ) -> None:
        self.payload = data.get("payload", {})
        self.variables = data.get("variables", {})


class GoogleTest(unittest.TestCase):
    """Google API Test Cases."""

    def get_google_instance(self, key: str, project_id: str) -> main.Google:
        """Construct and return a Google TTS instance."""
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
    def test_validate_request(self, key: str, project_id: str) -> None:
        """Test 'validate_request' method when fields missing or invalid."""
        with self.assertRaises(ValueError):
            self.get_google_instance(key, project_id)

    def test_speech(self) -> None:
        """Test speech method for successful TextToSpeech synthesis."""
        instance = self.get_google_instance("123", "123")
        # Set up mock
        with unittest.mock.patch.object(
            texttospeech.TextToSpeechClient, "synthesize_speech"
        ) as mock_synthesize_speech:
            mock_synthesize_speech.return_value.audio_content = (
                DECODED_RESULT_GOOGLE
            )
            # Call the speech method
            audio_bytes = instance.speech("hi", "en-US")
        # Assert the result
        self.assertEqual(audio_bytes, DECODED_RESULT_GOOGLE)

    def test_speech_invalid_credential(self) -> None:
        """Test credentials for speech method."""
        instance = self.get_google_instance(
            "WRONG_API_KEY",
            "WRONG_PROJECT_ID",
        )
        # Set up mock.
        with unittest.mock.patch.object(
            texttospeech.TextToSpeechClient, "synthesize_speech"
        ) as mock_synthesize_speech:
            # Raise Exception.
            mock_synthesize_speech.side_effect = Exception
        # Assert the raise.
        self.assertRaises(Exception, instance.speech, "hello", "en-US")

    def test_speech_invalid_language(self) -> None:
        """Test language for speech method."""
        instance = self.get_google_instance(
            "<YOUR_API_KEY>",
            "<YOUR_PROJECT_ID>",
        )
        # Set up mock.
        with unittest.mock.patch.object(
            texttospeech.TextToSpeechClient, "synthesize_speech"
        ) as mock_synthesize_speech:
            # Raise Exception.
            mock_synthesize_speech.side_effect = Exception
        # Assert the raise.
        self.assertRaises(Exception, instance.speech, "hello", "en-EN")

    def test_speech_no_text(self) -> None:
        """Test text-content for speech method."""
        instance = self.get_google_instance(
            "<YOUR_API_KEY>",
            "<YOUR_PROJECT_ID>",
        )
        # Set mock.
        with unittest.mock.patch.object(
            texttospeech, "TextToSpeechClient"
        ) as mock_client:
            # Raise Exception.
            mock_client.side_effect = Exception
        # Assert the raise.
        self.assertRaises(Exception, instance.speech, None, "en-US")


class AzureTest(unittest.TestCase):
    """Azure API Test Cases."""

    def get_azure_instance(self, key: str) -> main.Azure:
        """Set Azure instance with request."""
        req = MyRequest({
            "payload": {
                "provider": "azure",
                "text": "hi",
                "language": "en-US",
            },
            "variables": {
                "API_KEY": key,
            }
        })
        return main.Azure(req)

    def test_validate_request_missing_key(self) -> None:
        """Test 'validate_request' method when fields missing or invalid."""
        self.assertRaises(ValueError, self.get_azure_instance, None)

    def test_speech(self) -> None:
        """Test speech method for successful TextToSpeech synthesis."""
        instance = self.get_azure_instance("123")
        # Mock the requests.post method used in get_token.
        mock_response = MagicMock()
        mock_response.text = "fake_access_token"
        mock_response_request = MagicMock()
        mock_response_request.content = DECODED_RESULT_AZURE
        with unittest.mock.patch.object(
            requests, "post", return_value=mock_response
        ), unittest.mock.patch.object(
            requests, "request", return_value=mock_response_request
        ):
            # Call the speech method.
            got = instance.speech("hi", "en-US")
        # Assert the result.
        self.assertEqual(got, DECODED_RESULT_AZURE)

    def test_speech_invalid_credential(self) -> None:
        """Test credentials for speech method."""
        instance = self.get_azure_instance("WRONG_API_KEY")
        # Mock the requests.post method used in get_token.
        mock_response = requests.Response
        mock_response.text = "fake_access_token"
        with unittest.mock.patch.object(
            requests, "post", return_value=mock_response
        ), unittest.mock.patch.object(
            requests, "request", return_value=Exception("Error.")
        ):
            # Assert the raise.
            self.assertRaises(Exception, instance.speech, "hi", "en-US")


class AWSTest(unittest.TestCase):
    """AWS API Test Cases."""

    def get_aws_instance(self, key: str, secret_key: str) -> main.AWS:
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
    def test_validate_request(self, key: str, secret_key: str) -> None:
        """Test 'validate_request' method when fields missing or invalid."""
        self.assertRaises(ValueError, self.get_aws_instance, key, secret_key)

    def test_speech(self) -> None:
        """Test speech method for successful TextToSpeech synthesis."""
        instance = self.get_aws_instance("123", "123")
        # Set up mock.
        raw_stream = io.BytesIO(DECODED_RESULT_AWS)
        size = len(DECODED_RESULT_AWS)
        stream_body_obj = botocore.response.StreamingBody(raw_stream, size)
        with unittest.mock.patch.object(
            boto3.Session, "client"
        ) as mock_client:
            mock_client.return_value.synthesize_speech.return_value = {
                "AudioStream": stream_body_obj,
                "ContentType": "bytes",
                "RequestCharacters": 123,
            }
            got = instance.speech("hi", "en-US")
            want = DECODED_RESULT_AWS
        # Assert the result.
        self.assertEqual(got, want)

    def test_speech_key_exception(self) -> None:
        """Test speech method for exceptions during TextToSpeech synthesis."""
        instance = self.get_aws_instance("123", "123")
        # Assert the raise.
        self.assertRaises(Exception, instance.speech, "hi", "en-US")


class ValidateCommonTest(unittest.TestCase):
    """Test Cases for validate_common function."""

    def get_req(
        self,
        payload: str,
        variables: str,
        provider: str,
        text: str,
        language: str,
    ) -> None:
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

    def test_validate_common(self) -> None:
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
    def test_validate_common_errors(
        self,
        payload: str,
        variables: str,
        provider: str,
        text: str,
        language: str
    ) -> None:
        """Test validate common method when it raises value errors."""
        req = self.get_req(payload, variables, provider, text, language)
        # Assert the raise.
        self.assertRaises(ValueError, main.validate_common, req)


if __name__ == "__main__":
    unittest.main()
