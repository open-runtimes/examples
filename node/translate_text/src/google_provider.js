const translate = require('@vitalets/google-translate-api');

module.exports = async function(payload){
  const {
    text,
    from,
    to
  } = JSON.parse(payload);

  let response = await translate(text, {to: to, from: from}).then((data) => {
    return {message: data.text, success: true, from: from};
  }).catch((e) => {console.error(e); return {message: e.message, success: false}});
  return response;
}