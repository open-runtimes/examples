const mailgun = require("mailgun-js");

module.exports = async function (req, res) {
    const emailConfig = {
        apiKey: req.env.MAILGUN_API_KEY,
        domain: req.env.MAILGUN_DOMAIN
    };
    const mg = mailgun(emailConfig);
    
    // Get the name and email of the newly created user from Appwrite's environment variable
    const payload = JSON.parse(req.payload)
    const name = payload['name']
    const email = payload['email']
    
    // Create your email 
    const data = {
        from: 'Welcome to My Awesome App <welcome@my-awesome-app.io>',
        to: email,
        subject: `Welcome on board ${name}!`,
        text: `Hi ${name}\nGreat to have you with us. ! 😍`
    };
    
    // Send the email! ❤️
    mg.messages().send(data, function (error, body) {
        res.send(body);
    });
};