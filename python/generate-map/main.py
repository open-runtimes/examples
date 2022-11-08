from PIL import Image
import requests
from io import BytesIO
from base64 import b64encode
def error(res,message):
    return res.json({"success":False,"message":message})
def main(req,res):
     
     try:
        payload = req.payload
        lng = payload["lng"]
        lat = payload["lat"]
        access_token = req.variables["token"]
     except Exception:
        return error(res,"The payload must contain lng and lat or access token is not present")
     if lng == None or lat == None:
        return error(res,"The payload must contain lng and lat")
     elif((type(lng)!=float or type(lat)!=float) and (type(lng)!=int or type(lat)!=int)):
        return error(res,"The longitude and latitude must be a number")
     elif lng >180  or lng < -180 :
        return error(res,"The longitude must be between -180 and 180")
     elif lat >90  or lat < -90 :
        return error(res,"The latitude must be between -90 and 90")
     url ="https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/{0},{1},15,0,0/1000x1000?access_token={2}".format(lng,lat,access_token)
     response = requests.get(url,timeout=30)
     if response.status_code ==requests.codes.ok:
        with Image.open(BytesIO(response.content)) as img:
            byt = b64encode(img.tobytes())
            str1 = byt.decode() 
        return res.json({"success":True,"image":str1})
     else:
        return error(res,"Nothing to retrieve please check the url")
