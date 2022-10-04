const aws = require('aws-sdk')


module.exports = async function(payload,variables){
  const {
    text,
    source,
    target
  } = JSON.parse(payload);


  const {
    AWS_ACCESS_KEY,
    AWS_SECRET_KEY
  } = JSON.parse(variables);

  aws.config.region = 'us-east-1'
  aws.config.credentials = new aws.Credentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
  const params = {
    SourceLanguageCode: source,
    TargetLanguageCode: target,
    Text: text
  };
  const translateService = new aws.Translate()

  const translatedMsg = await translateService.translateText(params, (err, data) => {
    return data;
}).promise()

  return translatedMsg.TranslatedText;

}