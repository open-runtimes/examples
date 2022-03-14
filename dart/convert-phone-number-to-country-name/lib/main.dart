import 'dart:convert';
import 'dart:io';
import 'package:dart_appwrite/dart_appwrite.dart';

/*
Globally scoped cache used by function. Example value:
[
    {
        "code": "+1",
        "countryCode": "US",
        "countryName": "United States"
    }
]
*/
var phonePrefixList = null;

Future<void> start(final req, final res) async {
    // Input validation
    var phoneNumber = "";
    try {
        final payload = jsonDecode(req.payload);
        phoneNumber = payload['phoneNumber'].replaceAll(' ', '');
    } catch(err) {
        print(err);
        throw Exception('Payload is invalid.');
    }

    if(phoneNumber == null || !phoneNumber.startsWith('+')) {
        throw Exception('Invalid phone number.');
    }

    // Make sure we have envirnment variables required to execute
    if(
        req.env['APPWRITE_FUNCTION_ENDPOINT'] == null || 
        req.env['APPWRITE_FUNCTION_PROJECT_ID'] == null || 
        req.env['APPWRITE_FUNCTION_API_KEY'] == null
    ) {
        throw Exception('Please provide all required environment variables.');
    }

    // If we don't have cached list of phone number prefixes (first execution only)
    if(phonePrefixList == null) {
        // Init Appwrite SDK
        final client = new Client();
        final locale = new Locale(client);

        client
            .setEndpoint(req.env['APPWRITE_FUNCTION_ENDPOINT'] ?? '')
            .setProject(req.env['APPWRITE_FUNCTION_PROJECT_ID'] ?? '')
            .setKey(req.env['APPWRITE_FUNCTION_API_KEY'] ?? '');

        // Fetch and store phone number prefixes
        final serverResponse = await locale.getCountriesPhones();
        phonePrefixList = serverResponse.phones;
    }

    // Get phone prefix
    final phonePrefix = phonePrefixList.firstWhere((prefix) => phoneNumber.startsWith(prefix.code));

    if(phonePrefix == null) {
        throw Exception('Invalid phone number.');
    }

    // Return phone number prefix
    res.json({
        'phoneNumber': phoneNumber,
        'phonePrefix': phonePrefix.code,
        'countryCode': phonePrefix.countryCode,
        'countryName': phonePrefix.countryName
    });
}