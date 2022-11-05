const googleProvider = require('./google_provider')
const azureProvider = require('./azure_provider')
const awsProvider = require('./aws_provider');

module.exports = async function(req, res) {
    const {
      provider,
      text
    } = JSON.parse(req.payload);

    let response ={};

    if(provider==='google'){
      response = await googleProvider(req.payload).catch((e)=>console.log(e));
    }

    else if(provider==='azure'){
      response = await azureProvider(req.payload, req.variables).catch((e)=>console.log(e));
    }

    else if(provider==='aws'){
      response = await awsProvider(req.payload, req.variables).catch((e)=>console.log(e));
    }  
    
    else{
      response = {
        success : false,
        message : "The provider does not match the available providers"
      }
    }
    // console.log(response);
    res.json(response);
};