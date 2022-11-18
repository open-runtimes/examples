export default async function (req: any, res: any) {
    const APIkey = req.variables["DEEPGRAM_API_KEY"];
    const { fileUrl } = JSON.parse(req.payload);

    const response = await fetch(
        "https://api.deepgram.com/v1/listen",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Token ${APIkey}`,
            },
            body: JSON.stringify({ url: fileUrl }),
        }
    );

    if (!APIkey || !fileUrl) {
        return res.json('Please provide valid APIkey and fileUrl');
    }

    else {
        const error = await response.json();
        return res.json({
            success: false,
            message: error.reason,
        });
    }

    const data = await response.json();

    res.json({
        success: true,
        deepgramData: data,
    });
}