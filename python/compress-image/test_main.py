""" Unittests for Compress Image implementation in main.py"""
# Standard library
import base64
import pathlib
import unittest
from unittest.mock import patch

# Third party
import requests
import tinify
from parameterized import parameterized

# Local imports
import main
import secret

# Path to 1kb image (png/jpg/jpeg)
IMAGE = pathlib.Path("test/1kb.png").read_bytes()

# Path to tinypng encoded result (str)
RESULT_TINYPNG = (
    pathlib.Path("test/1kb_result_encoded_tinypng.txt").
    read_text(encoding="utf-8"))

# Path to krakenio encoded result (str)
RESULT_KRAKENIO = (
    pathlib.Path("test/1kb_result_encoded_krakenio.txt").
    read_text(encoding="utf-8"))


class TestTinypng(unittest.TestCase):
    """Class for testing the functionality of the "tinypng_impl" function."""
    @unittest.skipUnless(secret.API_KEY_TINYPNG, "No Tinypng API Key set")
    def test_tinypng_happy_path(self):
        """Test case optimizing 1kb image using "tinypng_impl" function."""
        want = RESULT_TINYPNG
        got = main.tinypng_impl(
            {
                "api_key": secret.API_KEY_TINYPNG,
                "decoded_image": IMAGE,
            }
        )
        self.assertEqual(base64.b64encode(got).decode(), want)

    def test_tinypng_credential(self):
        """Test case handling Account errors in the "tinypng_impl" function."""
        # Incorrect Credential
        self.assertRaises(
            tinify.errors.AccountError,
            main.tinypng_impl,
            {
                "api_key": "1NCORRECT4CREDENT1ALS",
                "decoded_image": IMAGE,
            }
        )

    @unittest.skipUnless(secret.API_KEY_TINYPNG, "No Tinypng API Key set.")
    @parameterized.expand([
        (b"",),
        (b"ORw0KGgoAAAANSUhEUgAAABEAAAAOCAMAAAD+M",),
    ])
    def test_tinypng_client(self, image):
        """Test case for Client errors in the "tinypng_impl" function."""
        # Image is empty
        data = {
            "api_key": secret.API_KEY_TINYPNG,
            "decoded_image": image,
        }
        self.assertRaises(tinify.errors.ClientError, main.tinypng_impl, data)

    @unittest.skipUnless(secret.API_KEY_TINYPNG, "No Tinypng API Key set.")
    @parameterized.expand([
        (
            {"a": secret.API_KEY_TINYPNG, "decoded_image": IMAGE},
        ),
        (
            {"api_key": secret.API_KEY_TINYPNG, "code_image": IMAGE},
        ),
        (
            {"": secret.API_KEY_TINYPNG, "code_image": IMAGE},
        ),
        (
            {"api_key": secret.API_KEY_TINYPNG, "": IMAGE},
        ),
    ])
    def test_tinypng_keys(self, data):
        """Test case for handling KeyError in the "tinypng_impl" function."""
        # Accessing wrong key
        self.assertRaises(
            KeyError,
            main.tinypng_impl,
            data,
        )

    @unittest.skipUnless(secret.API_KEY_TINYPNG, "No Tinypng API Key set.")
    def test_tinypng_variables(self):
        """Test case handling variables in the "tinypng_impl" function."""
        # Empty variables
        self.assertRaises(
            KeyError,
            main.tinypng_impl,
            {},
        )
        # One key in variable
        self.assertRaises(
            KeyError,
            main.tinypng_impl,
            {
                "api_key": secret.API_KEY_TINYPNG
            },
        )

    @unittest.skipUnless(secret.API_KEY_TINYPNG, "No Tinypng API Key set.")
    def test_tinypng_impl_basic_functionality_1kb(self):
        """Basic functionality of "tinypng_impl" with a 1kb image."""
        with patch.object(tinify, "from_buffer") as mock_from_buffer:
            # Set up the mock return value as decoded result
            mock_from_buffer.return_value.to_buffer.return_value = (
                base64.b64decode(RESULT_TINYPNG))
            # Assert the expected result
            optimized_image = main.tinypng_impl(
                {
                    "api_key": secret.API_KEY_TINYPNG,
                    "decoded_image": IMAGE,
                },
            )
            # Check if the return type is a byte
            self.assertIsInstance(optimized_image, bytes)
            # Check if the optimized_image equals mock_from_buffer return value
            self.assertEqual(
                optimized_image,
                mock_from_buffer.return_value.to_buffer.return_value)

    @unittest.skipUnless(secret.API_KEY_TINYPNG, "No Tinypng API Key set.")
    def test_tinypng_impl_unexpected_exception_account_error(self):
        """Test case handling unexpected "AccountError" in tinypng_impl."""
        with patch.object(tinify, "from_buffer") as mock_from_buffer:
            # Set up the mock return value as account exception
            mock_from_buffer.side_effect = (
                tinify.errors.AccountError("API Key is wrong"))
            # Check the raise for Account error
            self.assertRaises(
                tinify.errors.AccountError,
                main.tinypng_impl,
                {
                    "api_key": secret.API_KEY_TINYPNG,
                    "decoded_image": IMAGE,
                },
            )

    @unittest.skipUnless(secret.API_KEY_TINYPNG, "No Tinypng API Key set")
    def test_tinypng_impl_unexpected_exception_client_error(self):
        """Test case handling unexpected "ClientError" in tinypng_impl."""
        with patch.object(tinify, "from_buffer") as mock_from_buffer:
            # Set up the mock return value as client exception
            mock_from_buffer.side_effect = (
                tinify.errors.ClientError("Image is incorrect"))
            # Check the raise for Client error
            self.assertRaises(
                tinify.errors.ClientError,
                main.tinypng_impl,
                {
                    "api_key": secret.API_KEY_TINYPNG,
                    "decoded_image": IMAGE
                },
            )


class TestKrakenIO(unittest.TestCase):
    """Class for testing the functionality of the "krakenio_impl" function."""
    @unittest.skipUnless(
        secret.API_KEY_KRAKENIO and secret.SECRET_API_KEY_KRAKENIO,
        "No KrakenIO API Key or Secret Key.")
    def test_krakenio_happy_path(self):
        """Test case optimizing 1kb image using "krakenio_impl" function."""
        want = RESULT_KRAKENIO
        got = main.krakenio_impl(
            {
                "api_key": secret.API_KEY_KRAKENIO,
                "api_secret_key": secret.SECRET_API_KEY_KRAKENIO,
                "decoded_image": IMAGE
            },
        )
        self.assertEqual(got, base64.b64decode(want))

    @unittest.skipUnless(
        secret.API_KEY_KRAKENIO and secret.SECRET_API_KEY_KRAKENIO,
        "No KrakenIO API Key or Secret Key.")
    def test_krakenio_time_out(self):
        """Test case for KrakenIO Read Timeout."""
        with patch.object(requests, "post") as mock_post:
            mock_post.side_effect = requests.exceptions.ReadTimeout
            self.assertRaises(
                requests.exceptions.ReadTimeout,
                main.krakenio_impl,
                {
                    "api_key": secret.API_KEY_KRAKENIO,
                    "api_secret_key": secret.SECRET_API_KEY_KRAKENIO,
                    "decoded_image": IMAGE
                },
            )


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


class TestValidateRequest(unittest.TestCase):
    """Class for unittests testing validating requests."""
    @parameterized.expand([
        [
            {
                "payload": {
                    "provider": "tinypng",
                    "image": base64.b64encode(IMAGE).decode()
                },
                "variables": {"API_KEY": secret.API_KEY_TINYPNG},
            },
            {
                "provider": "tinypng",
                "api_key": secret.API_KEY_TINYPNG,
                "decoded_image": IMAGE
            }
        ],
        [
            {
                "payload": {
                    "provider": "krakenio",
                    "image": base64.b64encode(IMAGE).decode()
                },
                "variables": {
                    "API_KEY": secret.API_KEY_KRAKENIO,
                    "SECRET_API_KEY": secret.SECRET_API_KEY_KRAKENIO
                }
            },
            {
                "provider": "krakenio",
                "api_key": secret.API_KEY_KRAKENIO,
                "api_secret_key": secret.SECRET_API_KEY_KRAKENIO,
                "decoded_image": IMAGE
            }
        ]
    ])
    @unittest.skipUnless(
        secret.API_KEY_KRAKENIO and
        secret.SECRET_API_KEY_KRAKENIO and
        secret.API_KEY_TINYPNG,
        "No Tinypng Key and KrakenIO Key.",
    )
    def test_validate_request(self, got, expected):
        """Unittest for correct request."""
        req = MyRequest(
            {
                "payload": got["payload"],
                "variables": got["variables"],
            },
        )
        self.assertEqual(main.validate_request(req), expected)

    @parameterized.expand([
        [
            {
                "payload": {},
                "variables": {},
            }
        ],
        [
            {
                "payload": {"provider": "IMNOTAPROVIDER", "image": ""},
                "variables": {"API_KEY": "1234567"},
            }
        ],
        [
            {
                "payload": {"provider": "krakenio", "image": ""},
                "variables": {},
            }
        ],
        [
            {
                "payload": {"provider": "tinypng", "image": "12345"},
                "variables": {"API_KEY": ""},
            }
        ],
        [
            {
                "payload": {"provider": "krakenio", "image": "12345"},
                "variables": {"API_KEY": "", "SECRET_API_KEY": ""},
            }
        ],
        [
            {
                "payload": {"provider": "krakenio", "image": "12345"},
                "variables": {"API_KEY": "123", "SECRET_API_KEY": ""},
            }
        ],
        [
            {
                "payload": {"WRONG_PROVIDER": "krakenio", "image": "12345"},
                "variables": {"API_KEY": "123", "SECRET_API_KEY": ""},
            }
        ],
        [
            {
                "payload": {"provider": "krakenio", "1Mage": "12345"},
                "variables": {"API_KEY": "123", "SECRET_API_KEY": ""},
            }
        ],
        [
            {
                "payload": {"provider": "krakenio", "1Mage": "12345"},
                "variables": {"NOT AN API": "123", "SECRET_API_KEY": ""},
            }
        ],
        [
            {
                "payload": {"provider": "krakenio", "1Mage": "12345"},
                "variables": {"API": "123", "SecretKey": ""},
            }
        ],
    ])
    def test_validate_request_value_error(self, got):
        """Unittest for testing value errors."""
        req = MyRequest(
            {
                "payload": got["payload"],
                "variables": got["variables"],
            },
        )
        self.assertRaises(ValueError, main.validate_request, req)


class TestMain(unittest.TestCase):
    """Class test for main function."""
    @unittest.skipUnless(secret.API_KEY_TINYPNG, "No Tinypng API Key set")
    def test_main_success(self):
        """Unittest for main function success json response."""
        want = {
            "success": True,
            "image": RESULT_TINYPNG
        }
        # Create a request
        req = MyRequest({
            "payload": {
                "provider": "tinypng",
                "image": base64.b64encode(IMAGE).decode()
            },
            "variables": {
                "API_KEY": secret.API_KEY_TINYPNG
            }
        })
        # Create a response object
        res = MyResponse()
        main.main(req, res)
        # Check the response
        got = res.json()
        self.assertEqual(got, want)

    def test_main_value_error(self):
        """Unittest for main function when a value error is raised."""
        want = {"success": False, "error": "Missing payload"}
        # Create a request
        req = MyRequest({"payload": {}, "variables": {}})
        # Create a response object
        res = MyResponse()
        main.main(req, res)

        # Check the response
        got = res.json()
        self.assertEqual(got, want)

    def test_main_exception(self):
        """Unittest case for main function when exception is raised."""
        # Create a request
        req = MyRequest({
            "payload": {
                "provider": "tinypng",
                "image": base64.b64encode(IMAGE).decode()
            },
            "variables": {
                "API_KEY": "wrong_api_key"
            }
        })
        # Create a response object
        res = MyResponse()  # Create a response object
        main.main(req, res)

        # Check the response
        got = res.json()
        self.assertFalse(got["success"])
        self.assertIn("AccountError", got["error"])


if __name__ == "__main__":
    unittest.main()
