import 'dart:convert';
import 'package:http/http.dart' as http;

Future<void> start(final req, final res) async {
  Map<String, dynamic> data = jsonDecode(req.payload);

  if (!data.containsKey("url")) {
    res.json({"success": false, "message": "Missing parametre url."});
  }

  final url = data["url"];

  if (req.env['SCREENLY_API_KEY'] == null) {
    res.json({
      "success": false,
      "message": "Missing Screenly API Key in environment variables"
    });
  }

  final api_key = req.env['SCREENLY_API_KEY'];

  var parsedUrl =
      Uri.parse("http://screeenly.com/api/v1?key=${api_key}&url=${url}");
  var response = await http.post(parsedUrl);

  if (response.statusCode == 200) {
    Map<String, dynamic> parsedBody = jsonDecode(response.body);
    res.json({"success": true, "screenshot": parsedBody["base64"]});
  } else {
    Map<String, dynamic> parsedBody = jsonDecode(response.body);
    res.json({"success": false, "screenshot": parsedBody["message"]});
  }
}
