import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

Future<void> start(final req, final res) async {
  final APIKey = req.env["CLOUDMERSIVE_API_KEY"];

  Map<String, dynamic> data = jsonDecode(req.payload);

  if (!data.containsKey("url")) {
    throw new Exception("Missing url");
  }

  if (req.env['CLOUDMERSIVE_API_KEY'] == null) {
    throw new Exception("Missing Cloudmersive API Key in environment variables");
  }

  final url = data["url"];

  // Download image into buffer
  var parsedUrl = Uri.parse(url);
  var response = await http.get(parsedUrl);

  if (response.statusCode != 200) {
    throw new Exception("Failed to download image");
  }

  var imageBuffer = response.bodyBytes;

  var request = new http.MultipartRequest('POST', Uri.parse("https://api.cloudmersive.com/image/recognize/detect-objects"))
    ..files.add(new http.MultipartFile.fromBytes('imageFile', imageBuffer))
    ..headers['Apikey'] = APIKey;

  // Call Cloudmersive OCR API
  var cloudmersiveResponse = await request.send();

  // Parse response
  var responseBody = await cloudmersiveResponse.stream.bytesToString();
  Map<String, dynamic> parsedBody = jsonDecode(responseBody);

  if (parsedBody["Objects"] == null) {
    throw new Exception("No objects detected");
  }

  res.json(parsedBody["Objects"][0]);
}