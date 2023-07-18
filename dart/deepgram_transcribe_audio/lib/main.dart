import 'dart:convert';
import 'package:http/http.dart' as http;

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
  var fileUrl;
  var deepgramApiKey;

  //getting data from payload
  try {
    final data = jsonDecode(req.payload);
    fileUrl = data["fileUrl"];
    deepgramApiKey = req.variables["DEEPGRAM_API_KEY"];
  } catch (err) {
    returnFailure(res, err.toString());
    return;
  }

  //checks
  if (fileUrl == null || deepgramApiKey == null) {
    if (fileUrl == null)
      returnFailure(res, "Please provide a file url in payload");
    else
      returnFailure(res, "Please provide the api key in environment variables");
    return;
  }

  //making the request
  try {
    final endPoint =
        Uri.parse("https://api.deepgram.com/v1/listen?model=general");
    final headers = {
      "Authorization": "Token $deepgramApiKey",
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
