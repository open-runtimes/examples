export default async function (env: any, message: string) {
  const webhook = env["DISCORD_WEBHOOK_URL"];
  console.log(webhook);

  const res = await fetch(webhook, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      content: message,
    }),
  });
  if (res.status !== 204) {
    console.log(res);
    return {
      success: false,
      error: "Failed to send message, check the webhook URL",
    };
  }

  return { success: true };
}
