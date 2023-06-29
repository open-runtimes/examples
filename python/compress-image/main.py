import base64
import tinify
import json
import os
import tempfile
# Example Function Payload
{
    "provider":"tinypng",
    "image":"iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="
}
# Successful Function Response
{
    "success":True,
    "image":"iVBORw0KGgoAAAANSUhE...o6Ie+UAAAAASU5CYII="
}
# Error Function Response
{
    "success":False,
    "message":"Input file is not an image."
}


def decode(encoded_value):
    decoded = base64.b64decode(encoded_value)
    return decoded

def encode(plaintext):
    encoded = base64.b64encode(plaintext)
    return encoded

def errorMessage(res, message):
  return res.json({
      "success": False,
      "message": "fail",
  })

'''
Scraps

    Read data from external websites
    # data = encodeExternal("python/compress-image/base64.txt")
    # print(data)


How to encode a local image
    # Encode the image 
    # with open("python/compress-image/dogs.jpeg", "rb") as image_file:
    #     encoded_string = encode(image_file.read())
'''


# Potential Curl Command 
# curl http://localhost:3000/ 
# -H "X-Internal-Challenge: secret-key" 
# -H "Content-Type: application/json" 
# -d '{"payload": {"provider":"tinypng","image":"iVBORw0KGgoAAAANSUhEUgAAAaQAAALiCAY...QoH9hbkTPQAAAABJRU5ErkJggg=="}, "variables": {"TINYPNG_API_KEY": "<YOUR_API_KEY>"}}'

def main(req, res):
    # Get the payload then grab the provider and image
    payload = req.payload
    provider = payload['provider']
    image = payload['image']

    # Get the variables (usually the api key)
    variables = req.variables

    # Grab the api key
    secret_key = variables['TINYPNG_API_KEY']
    # Set the tinify key as the api key
    tinify.key = secret_key

    # Decode the image data from base64
    decoded = base64.b64decode(image)

    # Create a temp directory inside of the current working directory. Prefix it as "temp"
    temp_dir = tempfile.mkdtemp(prefix='temp', dir=os.getcwd())

    # Generate a copy of the decoded image data as non_optimized.jpg
    file_path = os.path.join(temp_dir, "non_optimized.jpg")
    # I used with open, to write media file with the decoded base64 contents
    with open(file_path, "wb") as f:
        f.write(decoded)

    # Compress the non_optimized image 
    source = tinify.from_file(file_path)
    output_file_path = os.path.join(temp_dir, "optimized.jpg")
    source.to_file(output_file_path)

    # Package the optimized image and encode it
    with open(output_file_path, "rb") as image_file:
        encoded = base64.b64encode(image_file.read())
    return res.json(
        {
            "success:" : True,
            "image": str(encode)
        }
    )
# request = {"payload": {"provider":"tinypng","image":"iVBORw0KGgoAAAANSUhEUgAAABEAAAAOCAMAAAD+MweGAAADAFBMVEUAAAAAAFUAAKoAAP8AJAAAJFUAJKoAJP8ASQAASVUASaoASf8AbQAAbVUAbaoAbf8AkgAAklUAkqoAkv8AtgAAtlUAtqoAtv8A2wAA21UA26oA2/8A/wAA/1UA/6oA//8kAAAkAFUkAKokAP8kJAAkJFUkJKokJP8kSQAkSVUkSaokSf8kbQAkbVUkbaokbf8kkgAkklUkkqokkv8ktgAktlUktqoktv8k2wAk21Uk26ok2/8k/wAk/1Uk/6ok//9JAABJAFVJAKpJAP9JJABJJFVJJKpJJP9JSQBJSVVJSapJSf9JbQBJbVVJbapJbf9JkgBJklVJkqpJkv9JtgBJtlVJtqpJtv9J2wBJ21VJ26pJ2/9J/wBJ/1VJ/6pJ//9tAABtAFVtAKptAP9tJABtJFVtJKptJP9tSQBtSVVtSaptSf9tbQBtbVVtbaptbf9tkgBtklVtkqptkv9ttgBttlVttqpttv9t2wBt21Vt26pt2/9t/wBt/1Vt/6pt//+SAACSAFWSAKqSAP+SJACSJFWSJKqSJP+SSQCSSVWSSaqSSf+SbQCSbVWSbaqSbf+SkgCSklWSkqqSkv+StgCStlWStqqStv+S2wCS21WS26qS2/+S/wCS/1WS/6qS//+2AAC2AFW2AKq2AP+2JAC2JFW2JKq2JP+2SQC2SVW2Saq2Sf+2bQC2bVW2baq2bf+2kgC2klW2kqq2kv+2tgC2tlW2tqq2tv+22wC221W226q22/+2/wC2/1W2/6q2///bAADbAFXbAKrbAP/bJADbJFXbJKrbJP/bSQDbSVXbSarbSf/bbQDbbVXbbarbbf/bkgDbklXbkqrbkv/btgDbtlXbtqrbtv/b2wDb21Xb26rb2//b/wDb/1Xb/6rb////AAD/AFX/AKr/AP//JAD/JFX/JKr/JP//SQD/SVX/Sar/Sf//bQD/bVX/bar/bf//kgD/klX/kqr/kv//tgD/tlX/tqr/tv//2wD/21X/26r/2////wD//1X//6r////qm24uAAAA1ElEQVR42h1PMW4CQQwc73mlFJGCQChFIp0Rh0RBGV5AFUXKC/KPfCFdqryEgoJ8IX0KEF64q0PPnow3jT2WxzNj+gAgAGfvvDdCQIHoSnGYcGDE2nH92DoRqTYJ2bTcsKgqhIi47VdgAWNmwFSFA1UAAT2sSFcnq8a3x/zkkJrhaHT3N+hD3aH7ZuabGHX7bsSMhxwTJLr3evf1e0nBVcwmqcTZuatKoJaB7dSHjTZdM0G1HBTWefly//q2EB7/BEvk5vmzeQaJ7/xKPImpzv8/s4grhAxHl0DsqGUAAAAASUVORK5CYII="}, "variables": {"TINYPNG_API_KEY": "R4nM3B54NbHNcHblC0XXl0LZyV82PBgZ"}}
# response = None
# print( compressImage(request, response) )


# curl http://localhost:3000/ -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json" -d '{"payload": {"provider": "tinypng", "image":"iVBORw0KGgoAAAANSUhEUgAAABEAAAAOCAMAAAD+MweGAAADAFBMVEUAAAAAAFUAAKoAAP8AJAAAJFUAJKoAJP8ASQAASVUASaoASf8AbQAAbVUAbaoAbf8AkgAAklUAkqoAkv8AtgAAtlUAtqoAtv8A2wAA21UA26oA2/8A/wAA/1UA/6oA//8kAAAkAFUkAKokAP8kJAAkJFUkJKokJP8kSQAkSVUkSaokSf8kbQAkbVUkbaokbf8kkgAkklUkkqokkv8ktgAktlUktqoktv8k2wAk21Uk26ok2/8k/wAk/1Uk/6ok//9JAABJAFVJAKpJAP9JJABJJFVJJKpJJP9JSQBJSVVJSapJSf9JbQBJbVVJbapJbf9JkgBJklVJkqpJkv9JtgBJtlVJtqpJtv9J2wBJ21VJ26pJ2/9J/wBJ/1VJ/6pJ//9tAABtAFVtAKptAP9tJABtJFVtJKptJP9tSQBtSVVtSaptSf9tbQBtbVVtbaptbf9tkgBtklVtkqptkv9ttgBttlVttqpttv9t2wBt21Vt26pt2/9t/wBt/1Vt/6pt//+SAACSAFWSAKqSAP+SJACSJFWSJKqSJP+SSQCSSVWSSaqSSf+SbQCSbVWSbaqSbf+SkgCSklWSkqqSkv+StgCStlWStqqStv+S2wCS21WS26qS2/+S/wCS/1WS/6qS//+2AAC2AFW2AKq2AP+2JAC2JFW2JKq2JP+2SQC2SVW2Saq2Sf+2bQC2bVW2baq2bf+2kgC2klW2kqq2kv+2tgC2tlW2tqq2tv+22wC221W226q22/+2/wC2/1W2/6q2///bAADbAFXbAKrbAP/bJADbJFXbJKrbJP/bSQDbSVXbSarbSf/bbQDbbVXbbarbbf/bkgDbklXbkqrbkv/btgDbtlXbtqrbtv/b2wDb21Xb26rb2//b/wDb/1Xb/6rb////AAD/AFX/AKr/AP//JAD/JFX/JKr/JP//SQD/SVX/Sar/Sf//bQD/bVX/bar/bf//kgD/klX/kqr/kv//tgD/tlX/tqr/tv//2wD/21X/26r/2////wD//1X//6r////qm24uAAAA1ElEQVR42h1PMW4CQQwc73mlFJGCQChFIp0Rh0RBGV5AFUXKC/KPfCFdqryEgoJ8IX0KEF64q0PPnow3jT2WxzNj+gAgAGfvvDdCQIHoSnGYcGDE2nH92DoRqTYJ2bTcsKgqhIi47VdgAWNmwFSFA1UAAT2sSFcnq8a3x/zkkJrhaHT3N+hD3aH7ZuabGHX7bsSMhxwTJLr3evf1e0nBVcwmqcTZuatKoJaB7dSHjTZdM0G1HBTWefly//q2EB7/BEvk5vmzeQaJ7/xKPImpzv8/s4grhAxHl0DsqGUAAAAASUVORK5CYII="}, "variables": {"TINYPNG_API_KEY": "R4nM3B54NbHNcHblC0XXl0LZyV82PBgZ"}}'
# curl http://localhost:3000/ -H "X-Internal-Challenge: secret-key" -H "Content-Type: application/json" -d '{"variables": {},"payload": "{}","headers": {}}'