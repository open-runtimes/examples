import 'dart:convert';
import 'package:http/http.dart' as http;

Future<void> start(final req, final res) async {
  Map<String, dynamic> data = jsonDecode(req.payload);

  // Checking if url Parameter is present in the payload
  if (!data.containsKey("url")) {
    throw Exception("Missing parameter url.");
  }

  final url = data["url"];

  // Checking if SCREENLY_API_KEY is provided in Enviornment.
  if (req.env['SCREENLY_API_KEY'] == null) {
    throw Exception(
      "Missing Screenly API Key in environment variables"
    );
  }

  final api_key = req.env['SCREENLY_API_KEY'];

  var parsedUrl = Uri.parse("http://screeenly.com/api/v1/fullsize?key=${api_key}&url=${url}");

  // Sending Post request to Screenly to get the screenshot.
  var response = await http.post(parsedUrl);

  // Response if the request was a success
  if (response.statusCode == 200) {
    Map<String, dynamic> parsedBody = jsonDecode(response.body);
    res.json({"success": true, "screenshot": parsedBody["base64"]});
  } 
  
  // Response otherwise
  else {
    Map<String, dynamic> parsedBody = jsonDecode(response.body);
    res.json({"success": false, "screenshot": parsedBody["message"]});
  }
}
