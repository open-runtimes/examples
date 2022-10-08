from PIL import Image
import requests
from io import BytesIO
from base64 import b64encode
access_token = "pk.eyJ1IjoicGFydGlrIiwiYSI6ImNsMjJ2MnNiZjB6aXMzb2pyMnZud3g2M2QifQ.lAIENJPfnGNpN2kG_w7kmQ"
token=""
data ={"lng":106.742650,"lat":30.730150 }
def error(res,message):
    return res.json({"success":False,"error":message})
def function(req,res):
     lng = data["lng"]
     lat = data["lat"]
     try:
        payload = json.loads(req.payload)
        lng = payload["lng"]
        lat = payload["lat"]
     except Exception:
        return error(res,"The payload must contain lng and lat")
     if(lng == None or lat == None):
        return error(res,"The payload must contain lng and lat")
     elif((type(lng)!=float or type(lat)!=float) and (type(lng)!=int or type(lat)!=int)):
        return error(res,"The longitude and latitude must be a number")
     elif(lng >180  or lng < -180 ):
        return error(res,"The longitude must be between -180 and 180")
     elif(lat >90  or lat < -90 ):
        return error(res,"The latitude must be between -90 and 90")
     url ="https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/{0},{1},15,0,0/1000x1000?access_token={2}".format(lng,lat,access_token)
     response = requests.get(url,json=body,timeout=30)
     if(response.status_code ==requests.codes.ok):
        with Image.open(BytesIO(response.content)) as img:
            str = b64encode(img.tobytes())
        return res.json({"success":True,"image":str})
     else:
        return error(res,"Nothing to retrieve please check the url")
