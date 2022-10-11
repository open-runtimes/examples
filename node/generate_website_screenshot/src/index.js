const {
    Builder
} = require("selenium-webdriver");
require("chromedriver");

const fetch = (...args) =>
    import("node-fetch").then(({ default: fetch }) => fetch(...args));


module.exports = async function (req, res) {
    const url = JSON.parse(req.payload ?? "{}").url;

    // check if the url exists
    async function checkLink(url) {
        try {
            await fetch(url).ok;
        } catch (e) {
            res.json({
                success: false,
                message: "Website could not be reached.",
            });
        }
    }

    // if url exist
    if (checkLink(url)) {
        let driver = await new Builder().forBrowser("chrome").build();

        await driver.get(url);

        let image = await driver.takeScreenshot();

        await driver.quit();

        res.json({
            success: true,
            screenshot: image,
        });
    }
};
