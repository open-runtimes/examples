import boto3
import secret
import base64

polly_client = boto3.Session(
    aws_access_key_id=secret.aws_access_key_id,       
    aws_secret_access_key=secret.aws_secret_access_key,
    region_name='us-west-2').client('polly')

t = "Hello, this is a sample text to be converted into speech."

response = polly_client.synthesize_speech(
    VoiceId='Joanna',
    OutputFormat='mp3',
    SampleRate='8000',
    Text=t,
    TextType='text',
    )

print(base64.b64encode(response['AudioStream'].read().decode()))