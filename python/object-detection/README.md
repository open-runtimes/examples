# 📷 Object Detection using Cloudmersive Vision API

A sample Python Cloud Function for object detection from an image URL. 

```json
{
    "payload": "{
        \"url\": \"URL_TO_AN_IMAGE\",
    }",
    "env": {
        "CLOUDMERSIVE_API_KEY": "YOUR_CLOUDMERSIVE_API_KEY"
    }
}
```

_Example output:_

```json
[
    {
        "name":"cake",
        "confidence":0.7977721691131592,
        "x":21,
        "y":5,
        "width":494,
        "height":333,
    }
]
```

## 🚀 Deployment

Copy this function folder somewhere and follow the instructions in the [Python runtime readme](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/python-3.10#readme) to deploy it.

## 📝 Notes
 - This function is designed for use with Open Runtimes. You can learn more about it [here](https://github.com/open-runtimes/open-runtimes).
 - This example is compatible with Python 3.8, 3.9, and 3.10.