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
  const APIkey = req.variables["SCREEN_SHOT_API_KEY"];
  try{
    const { url } = JSON.parse(req.payload);


  }catch(error){
    res.json({ success: false, message: "Invalid JSON body" });
    return;
  }
  if (!url) {
    res.json({ success: false, message: "URL is required" });
    return;
  }
  if (!APIkey) {
    res.json({ success: false, message: "API key is required" });
    return;
  }
  const fetchURL = `https://shot.screenshotapi.net/screenshot?token=${APIkey}&url=${url}&output=image&file_type=png&wait_for_event=load`;
  try{
    const response: { success: boolean; screenshot: string; error: string } =
    await getBase64FromUrl(fetchURL);
      res.json({ success: true, screenshot: response.screenshot });
      return;
  }catch(error){
    res.json({ success: false, message: error });
    return;

  }

 
}
