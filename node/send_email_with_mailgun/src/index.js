const sdk = require("node-appwrite");
const axios = require("axios").default;

/*
  'req' variable has:
    'headers' - object with request headers
    'payload' - object with request body data
    'env' - object with environment variables

  'res' variable has:
    'send(text, status)' - function to return text response. Status code defaults to 200
    'json(obj, status)' - function to return JSON response. Status code defaults to 200

  If an error is thrown, a response with code 500 will be returned.
*/

module.exports = async function (req, res) {
    const domain = req.env["MAILGUN_DOMAIN"];
    const apiKey = req.env["MAILGUN_API_KEY"];

    // Validate Domain
    if ((RegExp("^((?!-))(xn--)?[a-z0-9][a-z0-9-_]{0,61}[a-z0-9]{0,1}\.(xn--)?([a-z0-9\-]{1,61}|[a-z0-9-]{1,30}\.[a-z]{2,})$").test(domain) == false) {
        return res.send("Invalid domain", 400);
    }

    const mailgun = MailgunMailer(domain, apiKey);

};

class MailgunMailer {
    constructor(domain, apiKey) {
        this.domain = domain;
        this.apiKey = apiKey;
    }

    async send(from, to, cc, bcc, attachments, subject, html, text, template, options) {
        const client = axios.create({baseURL: "https://api.mailgun.net/v3"});
        
        const request = client.request({
            method: "POST",
            url: `/${this.domain}/messages`,
            auth: {
                username: "api",
                password: this.apiKey
            },
            headers: Object.fromEntries(Object.entries({
                "Content-Type": "multipart/form-data",
                "X-Mailgun-Variables": options.has("template_variables") ? JSON.stringify(options.get("template_variables")) : null
            }).filter(([_, v]) => v != null)), // Remove any null keys,
            data: Object.fromEntries(Object.entries({
                subject: subject,
                html: html,
                text: text,
                from: from,
                to: to.length > 0 ? to.join(", ") : null,
                cc: cc.length > 0 ? cc.join(", ") : null,
                bcc: bcc.length > 0 ? bcc.join(", ") : null,
                template: template
            }).filter(([_, v]) => v != null)) // Remove any null keys
        })
    }
}