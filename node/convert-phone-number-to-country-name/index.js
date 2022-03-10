const sdk = require('node-appwrite');

/*
Globaly-scoped cache used by function. Example value:
[
    {
        "code": "+1",
        "countryCode": "US",
        "countryName": "United States"
    }
]
*/
let phonePrefixList;

module.exports = async (req, res) => {
    // Input validation
    let phoneNumber;
    try {
        const payload = JSON.parse(req.payload);
        phoneNumber = payload.phoneNumber.split(' ').join('');
    } catch(err) {
        console.log(err);
        throw new Error('Payload is invalid.');
    }

    if(!phoneNumber || !phoneNumber.startsWith('+')) {
        throw new Error('Invalid phone number.');
    }

    // Make sure we have envirnment variables required to execute
    if(
        !req.env.APPWRITE_FUNCTION_ENDPOINT || 
        !req.env.APPWRITE_FUNCTION_PROJECT_ID || 
        !req.env.APPWRITE_FUNCTION_API_KEY
    ) {
        throw new Error('Please provide all required environment variables.');
    }

    // If we don't have cached list of phone number prefixes (first execution only)
    if(!phonePrefixList) {
        // Init Appwrite SDK
        const client = new sdk.Client();
        const locale = new sdk.Locale(client);

        client
            .setEndpoint(req.env.APPWRITE_FUNCTION_ENDPOINT)
            .setProject(req.env.APPWRITE_FUNCTION_PROJECT_ID)
            .setKey(req.env.APPWRITE_FUNCTION_API_KEY);

        // Fetch and store phone number prefixes
        const serverResponse = await locale.getCountriesPhones();
        phonePrefixList = serverResponse.phones;
    }

    // Get phone prefix
    const phonePrefix = phonePrefixList.find((prefix) => phoneNumber.startsWith(prefix.code));

    if(!phonePrefix) {
        throw new Error('Invalid phone number.');
    }

    // Return phone number prefix
    res.json({
        phoneNumber,
        phonePrefix: phonePrefix.code,
        countryCode: phonePrefix.countryCode,
        countryName: phonePrefix.countryName
    });
};