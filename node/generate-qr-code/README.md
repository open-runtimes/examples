# 🔗 Generate QR Code

A function to generate QR codes from text using Open Runtimes.

## 🧰 Usage

### POST /

**Parameters**

| Name | Description               | Location | Type   | Sample Value          |
| ---- | ------------------------- | -------- | ------ | --------------------- |
| text | Text to encode in QR code | Body     | String | "https://appwrite.io" |

**Response**

Sample `200` Response:

```json
{
  "success": true,
  "message": "QR code generated successfully",
  "data": {
    "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
    "text": "https://appwrite.io",
    "timestamp": "2025-07-20T10:30:00.000Z"
  }
}
```
