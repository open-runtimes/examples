export default async function (
  variables: any,
  email: string,
  message: string,
  subject: string
) {
  const domain = variables["MAILGUN_DOMAIN"];
  const apiKey = variables["MAILGUN_API_KEY"];

  if (!domain) {
    return {
      success: false,
      message: "MAILGUN_DOMAIN is not set",
    };
  }
  if (!apiKey) {
    return {
      success: false,
      message: "MAILGUN_API_KEY is not set",
    };
  }

  const form = new FormData();
  form.append("from", "<welcome@my-awesome-app.io>");
  form.append("to", email);
  form.append("subject", subject);
  form.append("text", message);

  const res = await fetch(`https://api.mailgun.net/v3/${domain}/messages`, {
    method: "POST",
    headers: {
      Authorization: `Basic ${btoa("api:" + apiKey)}`,
    },
    body: form,
  });

  // catch errors
  if (!res.ok) {
    // const error = await res.json();
    return {
      success: false,
      message: `Error ${res.status}: ${res.statusText}`,
    };
  }

  return { success: true };
}
