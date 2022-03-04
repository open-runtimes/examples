<?

require_once('vendor/autoload.php');

use Appwrite\Client;
use Appwrite\Services\Locale;


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
$phonePrefixList = null;

return function($req, $res) use ($phonePrefixList) {
    // Input validation
    $phoneNumber = null;
    try {
        $payload = \json_decode($req['payload'], true);
        $phoneNumber = \trim($payload['phoneNumber']);
    } catch(\Exception $err) {
        \var_dump($err);
        throw new \Exception('Payload is invalid.');
    }

    if(empty($phoneNumber) || !\str_starts_with($phoneNumber, '+')) {
        throw new \Exception('Invalid phone number.');
    }

    // Make sure we have envirnment variables required to execute
    if(
        empty($req['env']['APPWRITE_FUNCTION_ENDPOINT']) || 
        empty($req['env']['APPWRITE_FUNCTION_PROJECT_ID']) || 
        empty($req['env']['APPWRITE_FUNCTION_API_KEY'])
    ) {
        throw new \Exception('Please provide all required environment variables.');
    }

    // If we don't have cached list of phone number prefixes (first execution only)
    if(empty($phonePrefixList)) {
        // Init Appwrite SDK
        $client = new Client();
        $locale = new Locale($client);

        $client
            ->setEndpoint($req['env']['APPWRITE_FUNCTION_ENDPOINT'])
            ->setProject($req['env']['APPWRITE_FUNCTION_PROJECT_ID'])
            ->setKey($req['env']['APPWRITE_FUNCTION_API_KEY']);

        // Fetch and store phone number prefixes
        $serverResponse = $locale->getCountriesPhones();
        $phonePrefixList = $serverResponse['phones'];
    }

    // Get phone prefix
    $phonePrefix = \current(\array_filter($phonePrefixList, function($prefix) use ($phoneNumber) {
        return \str_starts_with($phoneNumber, $prefix['code']);
    }));

    if(empty($phonePrefix)) {
        throw new \Exception('Invalid phone number.');
    }

    // Return phone number prefix
    $res->json([
        'phoneNumber' => $phoneNumber,
        'phonePrefix' => $phonePrefix['code'],
        'countryCode' => $phonePrefix['countryCode'],
        'countryName' => $phonePrefix['countryName']
    ]);
};