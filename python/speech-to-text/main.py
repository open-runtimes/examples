import os
import json
import wave
import base64
from googleMain import google_speech_recognition
from amazonMain import amazon_recognize
from azureSpeech import azure_recognize

def write_wav(audioBase64, fileName):
    base_path = os.path.dirname(os.path.abspath(__file__))
    filePath = os.path.join(base_path, "tempAudio",fileName)

    try :
        wav_file = open(filePath, "wb")
        decode_string = base64.b64decode(audioBase64)
        wav_file.write(decode_string)

        obj = wave.open(filePath, 'r')
        channels = obj.getnchannels()
        rate = obj.getframerate()
    except Exception as e:
        raise Exception(f'Error in processing wav file from base64: {str(e)}')

    return rate, channels, filePath


def main(req, res, key):
    errFlag = False
    errMsg = ''
    text_result = ""
    WAVE_ENCODING = "LINEAR16"

    try:
        payload = json.loads(req)
        provider = payload['provider']
        language = payload['language']


        supported_providers = ['google', 'aws', 'azure']
        if provider not in supported_providers:
            raise Exception(f'This ({provider}) provider is not supported')

        #write wav file and get info
        audio = payload['audio']
        sample_rate, channels, filePath = write_wav(audio, "tempo.wav")

        if provider == 'google':
            google_cred_key = key["GOOGLE_APPLICATION_CREDENTIALS"]
            text_result = google_speech_recognition(language, sample_rate, channels, filePath, google_cred_key)
        
        elif provider == 'aws':
            region = key['region']
            acces_key_id = key['aws_access_key_id']
            aws_secret_access_key = key['aws_secret_access_key']
            text_result = amazon_recognize(sample_rate, channels, language, filePath, region, acces_key_id, aws_secret_access_key)
        
        elif provider == 'azure':
            azure_speech_key = key["azure_speech_key"]
            region = key['region']
            text_result = azure_recognize(language, WAVE_ENCODING,  filePath, azure_speech_key, region)
        os.remove(filePath) #cleaning the audio file.

    except Exception as e:
        # raise Exception(f"Error: {(str(e))} ")
        errFlag = True
        errMsg = str(e)

    if text_result == "" or errFlag:
        if errMsg == "":
            errMsg = "Target language does not exist in iso639-2 format."
        response =  {
        'success': False,
        'message': errMsg
        }
        return response

    success = True
    response =  {
            'success': success,
            'text': text_result,
        }
    # print(response)
    return res.json(response)
