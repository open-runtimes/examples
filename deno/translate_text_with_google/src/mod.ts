import { GTR } from "https://deno.land/x/gtr/mod.ts";

export default async function (req: any, res: any) {
  const gtr = new GTR();

  //  Get the query parameters from Appwrite's environment variable
  const payload = JSON.parse(req.payload);
  const { text, source, target } = payload;

  // Send request
  try {
    const { trans } = await gtr.translate(
        text,
        { 
            sourceLang: source,
            targetLang: target 
        },
      );

    // Parse response
    res.send({
        text: text,
        translation: trans
    });
  } catch (e) {
    console.error(e);
  }
}
