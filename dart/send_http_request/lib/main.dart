import "dart:convert";

import "package:dio/dio.dart";

Future<void> sendHttpRequest(final res, String url, String method, Map<String, dynamic> headers, String body) async {
  try {
    final dio = Dio();
    RequestOptions requestOptions = RequestOptions(path: url.trim(), method: method, headers: headers, data: body);

    var response = await dio.fetch(requestOptions);

    if (response.statusCode == 200) {
      markSuccess(res, {
        "headers": response.headers.map,
        "code": 200,
        "body": response.data,
      });
      return;
    } else {
      markFailure(res, "URL could not be reached"); // Response message could be changed based on code received.
    }
  } catch (e) {
    markFailure(res, "URL could not be reached");
  }
}

/// Sends a response back to user indicating a failure.

void markFailure(final res, final message) {
  res.json({
    "success": false,
    "message": message,
  });
}

/// Sends a response back to user indicating a success along with [response].
/// [response] includes response headers, response code and response data.

void markSuccess(final res, final response) {
  res.json({
    "success": true,
    "response": response,
  });
}

Future<void> start(final req, final res) async {
  String url = "";
  String method = "";
  Map<String, dynamic> headers;
  String body;

  try {
    final payload = jsonDecode(req.payload);
    url = payload["url"] ?? '';
    method = payload["method"] ?? '';
    headers = payload["headers"] != null ? Map<String, dynamic>.from(payload["headers"]) : {};
    body = payload["body"] ?? '';

    /// [url] and [method] are mandatory to send HTTP request.
    /// If any of the two isEmpty, then throw Error and terminate further code execution.

    if (url.isEmpty || method.isEmpty) {
      throw Error();
    }
  } catch (_) {
    markFailure(res, "Payload is invalid");
    return;
  }

  await sendHttpRequest(res, url, method, headers, body);
}
