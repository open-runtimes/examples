import 'dart:convert';
import 'dart:typed_data';
import 'package:http/http.dart' as http;

Future<void> _tinypng(
    final res, final Uint8List binaryImage, final String token) async {
  final reqUrl = Uri.parse("https://api.tinify.com/shrink");
  final headers = {
    "Authorization": "Basic ${base64.encode(utf8.encode('api:$token'))}",
  };

  try {
    var tinypngRes =
        await http.post(reqUrl, headers: headers, body: binaryImage);

    if (tinypngRes.statusCode == 200 || tinypngRes.statusCode == 201) {
      final output = jsonDecode(tinypngRes.body)['output'];
      final compressedImageURL = output['url'];
      final compressedImageBytes =
          await http.get(Uri.parse(compressedImageURL));
      markSuccess(res, base64Encode(compressedImageBytes.bodyBytes));
      return;
    } else {
      throw Exception(tinypngRes.reasonPhrase);
    }
  } catch (e) {
    print("Failure in tinpng $e");
    markFailure(res, e.toString());
    return;
  }
}

void markSuccess(final res, final String image) {
  res.json({
    'success': true,
    'image': image,
  });
}

void markFailure(final res, final String message) {
  res.json({
    'success': false,
    'message': message,
  });
}

Future<void> start(final request, final resonse) async {
  late final payload;
  late final String provider, imageBase64, apiKey;
  late final Uint8List image;
  try {
    payload = jsonDecode(request.payload);
    provider = payload['provider'].toLowerCase();
    imageBase64 = payload['image'];
  } catch (err) {
    markFailure(resonse, "Payload is invalid");
    return;
  }

  try {
    image = base64Decode(imageBase64);
  } catch (err) {
    markFailure(resonse, imageBase64);
    return;
  }

  switch (provider) {
    case "tinypng":
      try {
        apiKey = request.variables["TINYPNG_API_KEY"];
      } catch (err) {
        markFailure(resonse, "TINYPNG_API_KEY is not provided");
        return;
      }
      await _tinypng(resonse, image, apiKey);
      break;
    default:
  }
}
