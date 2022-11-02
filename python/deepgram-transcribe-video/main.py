from deepgram import Deepgram
import asyncio


async def transribeVideo(api_key, payload):
    dg_client = Deepgram(api_key)

    source = {'url': payload['fileUrl']}

    try:
        response = await dg_client.transcription.prerecorded(source, {'punctuate': True, "model": 'video'})
    except Exception as e:
        return {'success': False, 'message': str(e)}

    return {'success': True, 'deepgramData': response}


def main(req, res):

    payload = req.payload

    api_key = req.variables['DEEPGRAM_API_KEY']

    if not api_key:
        res.json({'success': False, 'message': 'DEEPGRAM_API_KEY not set'})

    rs = asyncio.run(transribeVideo(api_key, payload))

    return res.json(rs)
