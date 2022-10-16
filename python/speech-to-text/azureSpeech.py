import os
import azure.cognitiveservices.speech as speechsdk

def azure_recognize(lang, encoding, filename, secret_key, region):
    speech_config = speechsdk.SpeechConfig(
        subscription=secret_key,
        region=region,
        speech_recognition_language=lang
    )
    audio_input = speechsdk.AudioConfig(filename=filename)
    speech_recognizer = speechsdk.SpeechRecognizer(
        speech_config=speech_config,
        audio_config=audio_input
    )
    result = speech_recognizer.recognize_once()

    return result.text if result.reason == speechsdk.ResultReason.RecognizedSpeech else None

# print(azure_recognize(stream_file, "en-US", "LINEAR16"))