import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

Future<void> start(final req, final res) async {
  final APIKey = req.env["GIPHY_API_KEY"];

  Map<String, dynamic> data = jsonDecode(req.payload);

  if (!data.containsKey("search")) {
    throw new Exception("Missing Search Query");
  }

  if (req.env['GIPHY_API_KEY'] == null) {
    throw new Exception("Missing Giphy API Key in environment variables");
  }

  final search = data["search"];

  // Download image into buffer
  var parsedUrl = Uri.parse("https://api.giphy.com/v1/gifs/search?api_key=${APIKey}&q=${search}&limit=1");
  var response = await http.get(parsedUrl);

  if (response.statusCode != 200) {
    throw new Exception("Failed to get GIF");
  }

  // Parse response
  Map<String, dynamic> parsedBody = jsonDecode(response.body);

  Map<String, dynamic> result = {
    "search": search,
    "url": parsedBody["data"][0]['url']
  };

  res.json(result);
}