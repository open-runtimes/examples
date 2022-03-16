export default async function(req: any, res: any) {
    const APIkey = req.env["CLOUDMERSIVE_API_KEY"]

    const { url } = JSON.parse(req.payload);

    if (!url) {
        throw new Error("URL is required");
    }

    if (!APIkey) {
        throw new Error("API key is required");
    }

    // Download the image
    const image = await fetch(url).then((response) => response.blob());

    // Run the image through the Cloudmersive OCR API
    var data = new FormData();
    data.append('imageFile', image, 'file');

    const response = await fetch("https://api.cloudmersive.com/image/recognize/detect-objects",
        {
            method: "POST",
            headers: {
                "Apikey": APIkey,
            },
            body: data,
        });
    
    // Check status code
    if (response.status !== 200) {
        throw new Error(`Status code: ${response.status}, Data: ${await response.text()}`);
    }

    // Parse the response
    const dataJson = await response.json();
    
    res.json(dataJson["Objects"]);
}