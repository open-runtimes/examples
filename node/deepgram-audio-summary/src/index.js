const axios = require('axios').default;

module.exports = async (req, res) => {
  const APIkey = req.variables['DEEPGRAM_API_KEY'];
  const { fileUrl } = req.payload;

  if (!APIkey) {
    throw new Error('Deepgram API Key is required');
  }

  if (!fileUrl) {
    throw new Error('fileUrl is required');
  }

  const resp = await axios.post(
    'https://api.deepgram.com/v1/listen?summarize=true&punctuate=true',
    {
      url: fileUrl,
    },
    {
      headers: {
        'content-type': 'application/json',
        Authorization: `Token ${APIkey}`,
      },
    },
  );

  if (resp.status === 200) {
    res.json({
      success: true,
      deepgramData: resp.data.results,
    });
  } else {
    res.json({ success: false, message: 'Please provide a valid file URL.' });
  }
};
