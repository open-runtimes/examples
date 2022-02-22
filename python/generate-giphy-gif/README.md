# 🖼️ Generate Giphy Gif

A sample Python Cloud Function for generating Giphy GIF from the [Giphy API](https://developers.giphy.com/docs/api#quick-start-guide).

```json
{
    "payload": "{
        \"search\": \"Test\",
    }",
    "env": {
        "GIPHY_API_KEY": "YOUR_GIPHY_API_KEY"
    }
}
```

_Example output:_

```json
{
    "url": "https://giphy.com/gifs/test-gw3IWyGkC0rsazTi"
}
```

## 📝 Environment Variables

**GIPHY_API_KEY** - Your Giphy API key.

## 🚀 Deployment

Copy this function folder somewhere and follow the instructions in the [Python runtime readme](https://github.com/open-runtimes/open-runtimes/tree/main/runtimes/python-3.10#readme) to deploy it.

## 📝 Notes
 - This function is designed for use with Open Runtimes. You can learn more about it [here](https://github.com/open-runtimes/open-runtimes).
 - This example is compatible with Python 3.8, 3.9, and 3.10.