import 'dart:convert';
import 'package:http/http.dart' as http;

void returnFailure(final res, final msg) {
  res.json({
    "success": false,
    "message": msg,
  });
}

Future<void> start(final req, final res) async {
  Map<String, dynamic> data = jsonDecode(req.payload);

  if (!data.containsKey("lat")) {
    returnFailure(res, "The payload must contain latitude!");
    return;
  }

  if (!data.containsKey("lng")) {
    returnFailure(res, "The payload must contain longitude!");
    return;
  }

  final lat = data["lat"].toString();
  final lng = data["lng"].toString();

  final mapQuestAPIKey = req.env["MAPQUEST_API_KEY"];
  if (mapQuestAPIKey == null) {
    returnFailure(
        res, "MAPQUEST_API_KEY is not provided in the environment variables");
    return;
  }

  var doubleLat = double.tryParse(lat);
  var doubleLng = double.tryParse(lng);

  //checks
  if (doubleLat == null) {
    returnFailure(res, "Latitude must be a number");
    return;
  }
  if (doubleLng == null) {
    returnFailure(res, "Longitude must be a number");
    return;
  }
  if (doubleLng > 180 || doubleLng < -180) {
    returnFailure(res, "The longitude must be between -180 and 180");
    return;
  }
  if (doubleLat > 90 || doubleLat < -90) {
    returnFailure(res, "The latitude must be between -90 and 90");
    return;
  }

  //make the http request
  var parsedUrl = Uri.parse(
      "https://www.mapquestapi.com/staticmap/v5/map?key=$mapQuestAPIKey&center=$lat,$lng");
  var response = await http.get(parsedUrl);

  if (response.statusCode != 200) {
    returnFailure(res, "Failed to get the map: ${response.body}");
    return;
  }

  //Getting image
  var imageBuffer = response.bodyBytes;
  //converting to base64
  String base64Image = base64Encode(imageBuffer);

  Map<String, dynamic> result = {
    "success": true,
    "image": base64Image,
  };
  res.json(result);
}
