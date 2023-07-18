import { base64Encode } from "./deps.ts";

const getBase64FromUrl = async (url: string) => {
  const data = await fetch(url);

  if (data.ok) {
    const blob = await data.blob();
    const arr = await blob.arrayBuffer();
    const base64String = base64Encode(arr);
    return base64String;
  } else {
    throw new Error(await data.text());
  }
};

export default async function (req: any, res: any) {
  let APIkey: any;
  let url: any;

  try {
    const payload = JSON.parse(req.payload ?? '{}');
    url = payload.url;
  } catch(error) {
    res.json({ success: false, message: "Please provide URL." });
    return;
  }
  try {
    APIkey = req.variables["SCREEN_SHOT_API_KEY"];
  } catch(error) {
    res.json({ success: false, message: "Please API key." });
    return;
  }

  const fetchURL = `https://shot.screenshotapi.net/screenshot?token=${APIkey}&url=${url}&output=image&file_type=png&wait_for_event=load`;
  
  try {
    const response = await getBase64FromUrl(fetchURL);
    res.json({ success: true, screenshot: response });
    return;
  } catch(error) {
    res.json({ success: false, message: error });
    return;
  }
}
