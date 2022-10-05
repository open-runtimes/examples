

const getBase64FromUrl = async (url:any) => {
  const data = await fetch(url);
    const blob = await data.blob();
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.readAsDataURL(blob); 
      reader.onloadend = () => {
        const base64data = reader.result;   
        resolve({status:data.status, base64String:base64data});
      }
    });
    
  }


export default async function(req: any, res: any) {
  const APIkey = req.variables["SCREEN_SHOT_API_KEY"]

    const { url } = JSON.parse(req.payload);

    if (!url) {
        throw new Error("url is required");
    }

    if (!APIkey) {
        throw new Error("API key is required");
    }


  const fetchURL = `https://shot.screenshotapi.net/screenshot?token=${APIkey}&url=${url}&output=image&file_type=png&wait_for_event=load`
  const response:any = await getBase64FromUrl(fetchURL)
  // Check status code
  if (response.status !== 200) {
      res.json({"success":false,"message":"Website could not be reached."})
  }

  // Parse the response  
  res.json({"success":true, "screenshot":response.base64String});
}