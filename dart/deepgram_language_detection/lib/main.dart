import 'dart:convert';
import 'package:http/http.dart' as http;

//custom success message

void returnSuccess(final res, final data) {
  res.json({
    'success': true,
    'deepgramData': data,
  });
}

//custom failure message

void returnFailure(final res, final message) {
  res.json({
    'success': false,
    'message': message,
  });
}

Future<void> start(final req, final res) async {
  var fileUrl;
  var apiKey;

  //getting data from payload

  try {
    final data = jsonDecode(req.payload);
    fileUrl = data["fileUrl"];
    apiKey = req.variables["DEEPGRAM_API_KEY"];
  } catch (err) {
    returnFailure(res, err.toString());
    return;
  }

  //checks

  if (fileUrl == null) {
    returnFailure(res, "Provide a valid file URL in payload");
    return;
  }

  if (apiKey == null) {
    returnFailure(res, "Provide the API key as an environment variable");
  }

  //making the request

  try {
    final endPoint = Uri.parse(
        "https://api.deepgram.com/v1/listen?detect_language=true&punctuate=true");
    final headers = {
      "Authorization": "Token $apiKey",
      'content-type': 'application/json',
    };
    final body = {"url": fileUrl};
    final result =
        await http.post(endPoint, headers: headers, body: jsonEncode(body));
    if (result.statusCode != 200) {
      returnFailure(res, jsonDecode(result.body));
      return;
    }
    returnSuccess(res, jsonDecode(result.body));
    return;
  } catch (err) {
    returnFailure(res, err.toString());
    return;
  }
}
