import 'dart:convert';
import 'package:http/http.dart' as http;

Future<void> start(final req, final res) async {
  // Validating payload
  Map<String, dynamic> data;
  try {
    data = jsonDecode(req.payload);
  } on FormatException {
    res.json({
      "success": false,
      "message": "Invalid Payload.",
    });
    return;
  }

  // Checking if url Parameter is present in the payload
  if (!data.containsKey("url")) {
    res.json({
      "success": false,
      "message": "Missing parameter url.",
    });
    return;
  }

  final url = data["url"];

  // Checking if SCREEENLY_API_KEY is provided in Variables.
  if (req.variables['SCREEENLY_API_KEY'] == null) {
    res.json({
      "success": false,
      "message": "Missing Screeenly API Key in variables",
    });
    return;
  }

  final api_key = req.variables['SCREEENLY_API_KEY'];

  var parsedUrl = Uri.parse(
      "http://screeenly.com/api/v1/fullsize?key=${api_key}&url=${url}");

  // Sending Post request to Screenly to get the screenshot.
  var response = await http.post(parsedUrl);

  // Response if the request was a success
  if (response.statusCode == 200) {
    Map<String, dynamic> parsedBody = jsonDecode(response.body);
    res.json({"success": true, "screenshot": parsedBody["base64_raw"]});
  }

  // Response otherwise
  else {
    Map<String, dynamic> parsedBody = jsonDecode(response.body);
    final title = parsedBody['title'];
    final message = parsedBody['message'];
    res.json({"success": false, "message": '$title: $message'});
  }
}
