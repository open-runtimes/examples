const axios = require('axios').default;

module.exports = async function(payload, variables){
  const {
    text,
    source,
    target
  } = JSON.parse(payload);


  const {
    TRANSLATOR_KEY,
    LOCATION
  } = JSON.parse(variables);

  

  let key = TRANSLATOR_KEY;
  let endpoint = "https://api.cognitive.microsofttranslator.com";

    let location = LOCATION;

    const response = await axios({
        baseURL: endpoint,
        url: '/translate',
        method: 'post',
        headers: {
            'Ocp-Apim-Subscription-Key': key,
            'Ocp-Apim-Subscription-Region': location,
            'Content-type': 'application/json'
        },
        params: {
            'api-version': '3.0',
            'from': source,
            'to': [target]
        },
        data: [{
            'text': text
        }],
        responseType: 'json'
    })

    return response.data[0]['translations'][0]['text']
}