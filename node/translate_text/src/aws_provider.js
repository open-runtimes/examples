const aws = require('aws-sdk')


module.exports = async function(payload,variables){
  const {
    text,
    from,
    to
  } = JSON.parse(payload);


  const {
    AWS_ACCESS_KEY,
    AWS_SECRET_KEY
  } = JSON.parse(variables);

  aws.config.region = 'us-east-1'
  aws.config.credentials = new aws.Credentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
  const params = {
    SourceLanguageCode: from,
    TargetLanguageCode: to,
    Text: text
  };
  const translateService = new aws.Translate()

  const response = await translateService.translateText(params, (err, data) => {
    return data; 
}).promise().then(data => {return {success :true , message: data.TranslatedText, from: from};}).catch(e =>{return {success :false , message: e.message};} )

  return response;
}