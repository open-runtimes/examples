import os
import io
from google.cloud import speech

def google_speech_recognition(language, sample_rate, channels, filePath, secret_key):
    os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = secret_key
    client = speech.SpeechClient()
    # client = speech.SpeechClient.from_service_account_file()

    with io.open(filePath, "rb") as audio_file:
        audioContent = audio_file.read()

    try : 
        audio = speech.RecognitionAudio(content=audioContent)
        # this config supports (wav) LINEAR16(sp16) PMC encoding
        config = speech.RecognitionConfig(
            encoding=speech.RecognitionConfig.AudioEncoding.LINEAR16,
            sample_rate_hertz=sample_rate,
            language_code=language,
            audio_channel_count=channels,
        )
        # Detects speech in the audio file
        response = client.recognize(config=config, audio=audio, timeout=60)
    except Exception as e:
        print(f"Error from Google API{e}")
        raise Exception(f"Error from Google API{e}")
    text_result = ""
    for result in response.results:
        text_result += result.alternatives[0].transcript
    return text_result