export default async function (variables: any, message: string) {
  const webhook = variables["DISCORD_WEBHOOK_URL"];

  if (!webhook) {
    return {
      success: false,
      error: "DISCORD_WEBHOOK_URL is not set",
    };
  }

  const res = await fetch(webhook, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      content: message,
    }),
  });
  const data = await res.json();
  if (res.status !== 204) {
    return {
      success: false,
      error: data.message,
    };
  }

  return { success: true };
}
