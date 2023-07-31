import main
import test_main
import secret 

req = test_main.MyRequest({
    "payload": {
        "provider": "google",
        "text": "hi",
        "language": "en-US",
    },
    "variables": {
        "API_KEY": secret.GOOGLE_API_KEY,
        "PROJECT_ID": secret.GOOGLE_PROJECT_ID,
    }
})

res = test_main.MyResponse()

main.main(req, res)
print(res.json())