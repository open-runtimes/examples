const CloudmersiveImageApiClient = require('cloudmersive-image-api-client');
const axios = require('axios').default;

module.exports = async function(req, res) {
    let defaultClient = CloudmersiveImageApiClient.ApiClient.instance;
    let Apikey = defaultClient.authentications['Apikey'];
    Apikey.apiKey = req.variables["CLOUDMERSIVE_API_KEY"];
    let payload = JSON.parse(req.payload);

    let apiInstance = new CloudmersiveImageApiClient.RecognizeApi();

    let imageBlob = await axios.get(payload.url, { responseType: 'arraybuffer' });

    let recognizeDetectObjectsPromise = (imageBlob) => {
      return new Promise ((resolve, reject) => {
        apiInstance.recognizeDetectObjects(imageBlob, (error, data, response) => {
          if (error) return reject(error);
          resolve(data);
        });   
      })
    }

    let result = await recognizeDetectObjectsPromise(imageBlob.data);
  
    res.json(result.Objects);
};