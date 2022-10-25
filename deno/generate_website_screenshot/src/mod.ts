import { base64Encode } from "./deps.ts";

const getBase64FromUrl = async (url: string) => {
  const data = await fetch(url);
  if (data.ok) {
    const blob = await data.blob();
    const arr = await blob.arrayBuffer();
    const base64String = base64Encode(arr);
    return { success: true, screenshot: base64String };
  } else {
    return { success: false, screenshot: "" };
  }
};

export default async function (req: any, res: any) {
  const APIkey = req.variables["SCREEN_SHOT_API_KEY"];
  const { url } = JSON.parse(req.payload);
  if (!url || !APIkey) {
    res.json({ success: false, message: "Website could not be reached." });
    return;
  }
  const fetchURL = `https://shot.screenshotapi.net/screenshot?token=${APIkey}&url=${url}&output=image&file_type=png&wait_for_event=load`;
  const response:{success:boolean, screenshot:string} = await getBase64FromUrl(fetchURL);
  if (response.success) {
    res.json(response);
    return;
  }
  res.json({ success: false, message: "Website could not be reached." });
}
