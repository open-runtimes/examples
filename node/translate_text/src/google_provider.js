const translate = require('@vitalets/google-translate-api');

module.exports = async function(payload){
  const {
    text,
    source,
    target
  } = JSON.parse(payload);

  let translation = await translate(text, {to: target, from: source}).then((data) => {
    return data;
  }).catch((e) => console.error(e));
  
  return translation.text;
}