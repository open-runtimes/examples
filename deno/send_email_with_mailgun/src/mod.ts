export default async function(req: any, res: any) {
  // Create a new MailGunClient and pass it your API key and API domain
  const domain = req.env["MAILGUN_DOMAIN"];
  const apiKey = req.env["MAILGUN_API_KEY"];

  // Get the name and email of the newly created user from Appwrite's environment variable
  const payload = JSON.parse(req.payload);
  const userName = payload["name"];
  const email = payload["email"];

  // Create your email
  const form = new FormData();
  form.append("from", "Welcome to My Awesome App <welcome@my-awesome-app.io>");
  form.append("to", email);
  form.append("subject", `Welcome on board ${userName}!`);
  form.append("text", `Hi ${userName}\nGreat to have you with us. ! 😍`);

  // Send the email! ❤️
  fetch(`https://api.mailgun.net/v3/${domain}/messages`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${btoa("api:" + apiKey)}`,
    },
    body: form,
  });

  res.send("Success");
}