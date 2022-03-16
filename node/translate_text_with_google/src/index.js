const translate = require('@vitalets/google-translate-api');

module.exports = async function(req, res) {
    const {
      text,
      source,
      target
    } = JSON.parse(req.payload);

    let translation = await translate(text, {to: target, from: source}).then((data) => {
      return data;
    }).catch((e) => console.error(e));
  
    res.json({
      text: text,
      translation: translation.text
    });
};