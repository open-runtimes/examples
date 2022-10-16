
# Speech To Text using Python (google, aws, azure)

### Example Input
```
{"provider":"aws","language":"en","audio":"iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="}
```

key
a dict which provides `key` and `region` of the asked cloud provider in payload like.
key = 
```
{        
    "GOOGLE_APPLICATION_CREDENTIALS":r"E:\Projects\speech-to-text\speech-364611-0c4743e21df2.json",
    "aws_access_key_id": "AKIAVJDDVDNFSAPCJK",    
    "aws_secret_access_key": "xXAmNVe000HiSOdf22dNi2qw122YD",
    "azure_speech_key": "6bcz1347581ce4c1485df491bga0e72e",
    "region":"centralIndia"
}
```


### Example Output
```
 {'success': True, 'text': "hi I'd like to buy a Chrome Cast I was wondering whether you could help me with that"}
```
```
 {'success': False, 'messagge': 'Error in processing wav file from base64. '}
```


### Breif of functions

Every provider has uncommon set of input requirements and some limitations.
Audio metadata such as `sample_rate`, `channels` and, specific`encoding` etc., is required which is accessible through `wave_write()` function in main.py. 
I am using (LINEAR16) encoding to write audio(bas64) data to wav file, which is [recommended](https://cloud.google.com/speech-to-text/docs/encoding).

### Limitations

#### Amazon
At the moment Amazon Transcibe requires the audio file to be in s3 and over [HTTP](https://docs.aws.amazon.com/transcribe/latest/dg/batch-med-transcription.html), which required handling of event stream encoding no direct API for batches.
So I used streaming method which comes with partial-transcript, for which I used simple stack logic in `amazon_recognize()` to remove repeating translations.

#### Azure
Azure speech to text works on small sample efforlessly but with big samples it might need some conditional model tuning.


