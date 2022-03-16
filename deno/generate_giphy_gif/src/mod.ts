export default async function(req: any, res: any) {
    const APIkey = req.env["GIPHY_API_KEY"]

    const { search } = JSON.parse(req.payload);

    if (!search) {
        throw new Error("search is required");
    }

    if (!APIkey) {
        throw new Error("API key is required");
    }

    // Fetch the gif
    const response = await fetch(`https://api.giphy.com/v1/gifs/search?api_key=${APIkey}&q=${search}&limit=1`,
        {
            method: "GET",
            headers: {
                "Apikey": APIkey,
            }
        });
    
    // Check status code
    if (response.status !== 200) {
        throw new Error(`Status code: ${response.status}, Data: ${await response.text()}`);
    }

    // Parse the response
    const dataJson = await response.json();
    
    res.json(dataJson["data"][0]['url']);
}