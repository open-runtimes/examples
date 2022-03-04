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