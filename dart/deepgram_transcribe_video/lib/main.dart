import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

void returnFailure(final res, final msg) {
  res.json({
    "success": false,
    "message": msg,
  });
}

Future<void> start(final req, final res) async {
  Map<String, dynamic> data = jsonDecode(req.payload);

  var APIkey = req.variables["DEEPGRAM_API_KEY"];
  var fileUrl = req.payload["fileUrl"];
  var parsedUrl = Uri.parse("https://api.deepgram.com/v1/listen?model=video");
  var response = await http.post(parsedUrl,
      headers: {HttpHeaders.authorizationHeader: 'Token $APIkey'},
      body: {"fileUrl": fileUrl});

  if (response.statusCode != 200) {
    returnFailure(res,
        "Failed to get the transcription! Please check the link or API key once.");
    return;
  }

  var deepgramData = await response.body;

  Map<String, dynamic> result = {
    "success": true,
    "deepgramData": deepgramData,
  };
  res.json(result);
}
