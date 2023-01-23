import json
from PIL import Image
import requests
from io import BytesIO
import base64

def error(res,message):
    return res.json({"success":False,"message":message})

def main(req,res):
     try:
        payload = json.loads('{}' if not req.payload else req.payload)
        lng = payload.get('lng', None)
        lat = payload.get('lat', None)
        access_token = req.variables["MAPBOX_ACCESS_TOKEN"]
     except Exception:
        return error(res,"The payload must contain lng and lat or access token is not present")
     if lng == None or lat == None:
        return error(res,"The payload must contain lng and lat")
     elif((type(lng)!=float or type(lat)!=float) and (type(lng)!=int or type(lat)!=int)):
        return error(res,"The longitude and latitude must be a number")
     elif lng >180  or lng < -180:
        return error(res,"The longitude must be between -180 and 180")
     elif lat >90  or lat < -90:
        return error(res,"The latitude must be between -90 and 90")

     url ="https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/{0},{1},15,0,0/1000x1000?access_token={2}".format(lng,lat,access_token)
     response = requests.get(url,timeout=30)

     if response.status_code ==requests.codes.ok:
        image = base64.b64encode(response.content)
        return res.json({"success":True,"image":image.decode("utf-8")})
     else:
        return error(res,"Error " + str(response.status_code) + ": " + str(response.content))
