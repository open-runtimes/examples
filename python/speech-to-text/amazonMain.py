import asyncio
import os
import aiofile
from amazon_transcribe.client import TranscribeStreamingClient
from amazon_transcribe.handlers import TranscriptResultStreamHandler
from amazon_transcribe.model import TranscriptEvent
from amazon_transcribe.utils import apply_realtime_delay

# REGION = ""

class MyEventHandler(TranscriptResultStreamHandler):
    async def handle_transcript_event(self, transcript_event: TranscriptEvent):
        # This handler can be implemented to handle transcriptions as needed.
        # Here's an example to get started.
        results = transcript_event.transcript.results
        for result in results:
            for alt in result.alternatives:
                value = alt.transcript
                #print(alt.transcript)

                text_stack.append(value)

                
async def basic_transcribe(SAMPLE_RATE, CHANNEL_NUMS, LANGUAGE, AUDIO_PATH, REGION):
    # Setup up our client with our chosen AWS region
    client = TranscribeStreamingClient(region=REGION)

    # Start transcription to generate our async stream
    stream = await client.start_stream_transcription(
        language_code=LANGUAGE,
        media_sample_rate_hz=SAMPLE_RATE,
        media_encoding="pcm",  #wave encoding
    )

    async def write_chunks(AUDIO_PATH):
        # NOTE: For pre-recorded files longer than 5 minutes, the sent audio
        # chunks should be rate limited to match the realtime bitrate of the
        # audio stream to avoid signing issues.
        async with aiofile.AIOFile(AUDIO_PATH, "rb") as afp:
            reader = aiofile.Reader(afp, chunk_size=CHUNK_SIZE)
            result = await apply_realtime_delay(
                stream, reader, BYTES_PER_SAMPLE, SAMPLE_RATE, CHANNEL_NUMS
            )
        #print(result)
        await stream.input_stream.end_stream()
        return result

    # Instantiate our handler and start processing events
    handler = MyEventHandler(stream.output_stream)
    await asyncio.gather(write_chunks(AUDIO_PATH), handler.handle_events())

def amazon_recognize(sample_rate, channel_nums, language, audio_path, region, aws_acces_key_id, aws_secret_access_key):
    global BYTES_PER_SAMPLE 
    global text_stack
    global CHUNK_SIZE
    BYTES_PER_SAMPLE = 2
    text_stack = []
    CHUNK_SIZE = 1024 * 8


    os.environ["aws_access_key_id"] = aws_acces_key_id
    os.environ['aws_secret_access_key'] = aws_secret_access_key

    loop = asyncio.get_event_loop()
    loop.run_until_complete(basic_transcribe(sample_rate, channel_nums, language, audio_path, region))
    loop.close()
    res = ""
    ans = []
    ans.append( text_stack[0] )
    #clearing partial-transcript
    for i in range(1, len(text_stack)-1):
        if ans[-1] in text_stack[i]:
            ans.pop()
            ans.append(text_stack[i])
        else:
            ans.append(text_stack[i])
    if len(ans)>0:
        res = " ".join(ans)

    #print(res)
    return res
