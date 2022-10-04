const googleProvider = require('./google_provider')
const azureProvider = require('./azure_provider')
const awsProvider = require('./aws_provider');

module.exports = async function(req, res) {
    const {
      provider,
      text
    } = JSON.parse(req.payload);

    let translatedText = "";

    if(provider==='google'){
      translatedText = await googleProvider(req.payload).catch((e)=>console.log(e));
    }

    else if(provider==='azure'){
      translatedText = await azureProvider(req.payload, req.env).catch((e)=>console.log(e));
    }

    else if(provider==='aws'){
      translatedText = await awsProvider(req.payload, req.env).catch((e)=>console.log(e));
    }    
  
    res.json({
      text: text,
      translation: translatedText
    });
};