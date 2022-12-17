import 'dart:async';
import 'dart:convert';
import 'package:http/http.dart' as https;

/* Example function payload

 {"fileUrl":"https://static.deepgram.com/examples/interview_speech-analytics.wav"}
Successful function response:

{"success":true,"deepgramData":{}}

Error function response:

{"success":false,"message":"Please provide a valid file URL."}
*/

void returnSuccess(final res, final data) {
  res.json({
    'success': true,
    'deepgramData': data,
  });
}

void returnFailure(final res, final message) {
  res.json({
    'success': false,
    'message': message,
  });
}

Future<void> start(final req, final res) async {
  String? fileUrl;
  final RegExp regex = RegExp(
    r'^(http(s?):)([/|.|\w|\s|-])*\.(?:wav|mp3|flac|ogg|wma)$',
  );
  final String deepgramTopicDetectionUrl =
      "https://api.deepgram.com/v1/listen?detect_topics=true&punctuate=true";

  String? deepgramApiKey;
  try {
    final payload = jsonDecode(req.payload);
    fileUrl = payload['fileUrl'];
    deepgramApiKey = payload['DEEPGRAM_API_KEY'];
  } catch (err) {
    print(err);
    returnFailure(res, err.toString());
  }

  if (fileUrl == null) {
    returnFailure(res, "Please provide a valid file URL.");
  } else if (deepgramApiKey == null) {
    returnFailure(res, "Please provide a valid deepgram API key");
  } else {
    //check if the url is valid using regex
    if (!regex.hasMatch(fileUrl)) {
      returnFailure(res, "Please provide a valid file URL.");
    } else {
      //http post request to deepgram
      final response = await https.post(
        Uri.parse(deepgramTopicDetectionUrl),
        headers: {
          "Authorization": "Token $deepgramApiKey",
          "Content-Type": "application/json",
        },
        body: jsonEncode({
          "url": fileUrl,
        }),
      );

      //check if the response is successful
      if (response.statusCode == 200) {
        returnSuccess(res, response.body);
      } else {
        returnFailure(res, response.body);
      }
    }
  }
}
