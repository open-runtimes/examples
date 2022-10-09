const {
    Builder
} = require("selenium-webdriver");
require("chromedriver");


module.exports = async function (req, res) {
    const url = req.body.url;

    // check if the url exists
    async function checkLink(url) {
        return (await fetch(url)).ok;
    }

    // if url exist
    if (checkLink) {
        let driver = await new Builder().forBrowser("chrome").build();

        await driver.get(url);

        let image = await driver.takeScreenshot();

        // grab domain name from the url
        const domainName = url.replace(/https?:\/\/(?:www\.)?/, "").split(".")[0];

        // save the screenshot by the grabbed domain name, eg. appwrite.png
        await fs.writeFileSync(`./${domainName}.png`, image, "base64");

        await driver.quit();

        res.json({
            success: true,
            screenshot: image,
        });
    } else {
        res.json({
            success: false,
            message: "Website could not be reached.",
        });
    }
};
