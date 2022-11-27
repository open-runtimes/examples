from deepgram import Deepgram
import asyncio
import json


async def transribeVideo(api_key, payload):

    dg_client = Deepgram(api_key)
    source = {'url': payload['fileUrl']}

    try:
        response = await dg_client.transcription.prerecorded(source, {'punctuate': True, "model": 'video'})
    except Exception as e:
        return {'success': False, 'message': str(e)}
    return {'success': True, 'deepgramData': response}


def main(req, res):
    try:
        payload = json.loads(req.payload)
        api_key = req.variables['DEEPGRAM_API_KEY']
        rs = asyncio.run(transribeVideo(api_key, payload))
        return res.json(rs)
    except Exception as e:
        return res.json({'success': False, 'message': str(e)})
