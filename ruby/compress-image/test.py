import requests
import json
image = "iVBORw0KGgoAAAANSUhEUgAAAAYAAAAIAQMAAADgCBx7AAAABlBMVEU4GIc5GIgnkiOaAAAAFUlEQVQI12M4wtDDwAOEDQwOQHgAABnYAynl+A4yAAAAAElFTkSuQmCC"
rkraken = 'iVBORw0KGgoAAAANSUhEUgAAAAYAAAAIAQMAAADgCBx7AAAABlBMVEU4GIc5GIgnkiOaAAAAFUlEQVR42mM4wtDDwAOEDQwOQHgAABnYAyl7ZHJaAAAAAElFTkSuQmCC'
url = "http://localhost:3000"

variables = {
  "TINYPNG_API": "ngQtfWb98F2JPgbLKL999kLT25GpxZdR",
  "KRAKENIO_KEY": "24bcfdb90f4ff60dfbf4ee6e5e2c6ca3",
  "KRAKENIO_SECRET": "dca33a6d709a7cde5098fd5c61602038e5d2f5a1"
}

headers = {
  "X-Internal-Challenge": "secret-key",
  "Content-Type": "application/json"
}

tinypng_pl = {"provider":"tinypng", "image":image}
kraken_pl = {"provider":"krakenio", "image":image}

def makeRequest (payload, variables=variables) :
  data = {"payload": json.dumps(payload), "env": variables}
  r = requests.post(url, headers=headers, json=data)
  content = r.json()
  return [r.status_code, content.get('success'), content.get('message'), content.get('image')]

def checkFor (expected) :
  def c (payload, variables=variables) :
    try :
      assert expected == makeRequest(payload, variables)
    except AssertionError as e:
      print("payload", payload, "variables", variables, " produces ", makeRequest(payload, variables))
      raise e
  return c

def testPayloadInvalid () :
  test = checkFor([200, False, 'Invalid Payload', None])
  test({"provider": "", "image": image})
  test({"image": image})
  test({"provider": "krakenio"})
  test({"image": ""})
  test({"provider": ""})
  test({})
  print("passed invalid payload test")

def testTinypngApi () :
  test = checkFor([200, False, "no API key was provided.", None])
  test(tinypng_pl, {})
  test(tinypng_pl, {"KRAKENIO_KEY":""})
  test(tinypng_pl, {"KRAKENIO_KEY": variables["KRAKENIO_KEY"], "KRAKENIO_SECRET": variables["KRAKENIO_SECRET"]})
  test = checkFor([200, False, "Invalid API.", None])
  test(tinypng_pl, {"TINYPNG_API": ""})
  test(tinypng_pl, {"TINYPNG_API": "not-a-key"})
  test(tinypng_pl, {"TINYPNG_API": "idk", "KRAKENIO_KEY": variables["KRAKENIO_KEY"], "KRAKENIO_SECRET": variables["KRAKENIO_SECRET"]})
  print("passed tinypng api test")

def testKrakenIOApi () :
  test = checkFor([200, False, "no API key was provided.", None])
  test(tinypng_pl, {})
  test(kraken_pl, {"TINYPNG_API": ""})
  test(kraken_pl, {"TINYPNG_API": variables['TINYPNG_API'], "KRAKENIO_SECRET": variables["KRAKENIO_SECRET"]})
  test(kraken_pl, {"TINYPNG_API": variables['TINYPNG_API'], "KRAKENIO_KEY": variables["KRAKENIO_KEY"]})
  test = checkFor([200, False, "Invalid API.", None])
  test(kraken_pl, {"KRAKENIO_KEY": "not-key", "KRAKENIO_SECRET": "not-secret"})
  test(kraken_pl, {"KRAKENIO_KEY": "not-key", "KRAKENIO_SECRET": variables["KRAKENIO_SECRET"]})
  test(kraken_pl, {"KRAKENIO_KEY": variables["KRAKENIO_KEY"], "KRAKENIO_SECRET": "not-secret"})
  print("passed krakenio api test")

def testNotImage () :
  test = checkFor([200, False, "Input file is not an image.", None])
  test({"provider":"tinypng", "image":"not-image"})
  test({"provider":"tinypng", "image":"iVBORw0KGgo...K5CYII="})
  test({"provider":"krakenio", "image":"not-image"})
  test({"provider":"krakenio", "image":"iVBORw0KGgo...K5CYII="})
  print("passed not image test")

def testTinypng():
  test = checkFor([200, True, None, image]) # the image was compressed, so expected no change
  test(tinypng_pl)
  print("passed tinypng test")

def testKrakenIO():
  test = checkFor([200, True, None, rkraken]) 
  test(kraken_pl)
  print("passed krakenio test")

if __name__ == "__main__" :
  testPayloadInvalid()
  testTinypngApi()
  testKrakenIOApi()
  testNotImage()
  testTinypng()
  testKrakenIO()

