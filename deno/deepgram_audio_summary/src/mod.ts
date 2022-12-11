export default async function (req: any, res: any) {
    const APIkey = req.variables["DEEPGRAM_API_KEY"];
    let fileUrl = "";

    try {
        fileUrl = JSON.parse(req.payload);
    } catch (error) {
        return res.json("Invalid JSON string");
    }

    if (!APIkey || !fileUrl) {
        return res.json("Please provide valid APIkey and fileUrl");
    }

    const response = await fetch(
        "https://api.deepgram.com/v1/listen?summarize=true&punctuate=true",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Token ${APIkey}`,
            },
            body: JSON.stringify({ url: fileUrl }),
        }
    ); 

    if(response.status !== 201) {
        const error = await response.json();
        return res.json({
            success: false,
            message: `Encountered an error trying to make a request: ${error.reason}`,
        });
    }

    const data = await response.json();
    res.json({
        success: true,
        deepgramData: data,
    });
}