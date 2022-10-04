import requests
import json

# bitly
argsSuccess = {"provider": "bitly", "url": "https://www.google.com/"}
argsFailure = {"provider": "bitly", "url": "httpswwwgooglecom"}
dataSuccess = {"payload": json.dumps(argsSuccess)}
dataFailure = {"payload": json.dumps(argsFailure)}
headers = {"X-Internal-Challenge": "secret-key"}

# Success
r = requests.post("http://localhost:3000/", data=json.dumps(dataSuccess), headers=headers)
print("---------------------")
print("Provider: bitly")
print("url: https://www.google.com/")
print(r.text)
print("---------------------")
print()

# Failure
r = requests.post("http://localhost:3000/", data=json.dumps(dataFailure), headers=headers)
print("---------------------")
print("Provider: bitly")
print("url: httpswwwgooglecom")
print(r.text)
print("---------------------")
print()


# tinyurl
argsSuccess = {"provider": "tinyurl", "url": "https://www.google.com/"}
argsFailure = {"provider": "tinyurl", "url": "httpswwwgooglecom"}
dataSuccess = {"payload": json.dumps(argsSuccess)}
dataFailure = {"payload": json.dumps(argsFailure)}
headers = {"X-Internal-Challenge": "secret-key"}

# Success
r = requests.post("http://localhost:3000/", data=json.dumps(dataSuccess), headers=headers)
print("---------------------")
print("Provider: tinyurl")
print("url: https://www.google.com/")
print(r.text)
print("---------------------")
print()

# Failure
r = requests.post("http://localhost:3000/", data=json.dumps(dataFailure), headers=headers)
print("---------------------")
print("Provider: tinyurl")
print("url: httpswwwgooglecom")
print(r.text)
print("---------------------")
print()