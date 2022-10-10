const axios = require("axios");
module.exports = async (req, res) => {
  let url, method, body, headers;

  try {
    const payload = JSON.parse(req.payload);
    url = payload.url;
    method = payload.method || undefined;
    headers = payload.headers || undefined;
    body = payload.body || undefined;
  } catch (err) {
  
    res.json({ success: false, message: "Payload does not have a proper structure." });
  }

  const config = {
    method,
    url,
    data: body,
    headers,
  };

  try {
    const response = await axios(config); // Request to Server

    res.json({
      success: true,
      response: {
        headers: response.headers,
        code: response.status,
        body: response.data,
      },
    });
  } catch (error) {
    res.json({ success: false, message: "URL could not be reached." });
  }
};
