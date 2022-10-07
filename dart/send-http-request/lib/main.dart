import 'dart:convert';
import 'package:dio/dio.dart' hide Response;

Future<void> start(final req, final res) async {

  if (req.payload == '') {
    res.send("No valid payload");
    return;
  }
  final payload = jsonDecode(req.payload);

  String url = payload['url'] ?? '';
  // Map<String, String> headers = payload['headers'] ?? {};
  String method = payload['method'] ?? '';
  // String body = payload['body'] ?? '';

  if (url.isEmpty || method.isEmpty) {
    res.send("No valid payload parameter values");
    return;
  }

  var response =
      await Dio().fetch(RequestOptions(path: url.trim(), method: method));

  if (response.statusCode != 200) {
    res.json({'success': false, 'message': "URL could not be reached."});
  }

  res.json({
    'success': true,
    'response': {
      "headers": {},
      "code": response.statusCode,
      "body": jsonEncode(response.data),
    },
  });

}
