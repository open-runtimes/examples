import 'dart:convert';
import 'package:http/http.dart' as http;

Future<void> bitly (final res, String url, String token) async {
  var reqUrl = Uri.parse("https://api-ssl.bitly.com/v4/shorten");

  var body = {
    "long_url": url,
  };

  var headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer $token",
  };

  var response = await http.post(reqUrl, body: json.encode(body), headers: headers);
  var resBody = json.decode(response.body);

  if (resBody["message"] == "INVALID_ARG_LONG_URL") {
    markFailure(res, "Invalid URL");
    return;
  } else if (resBody["message"] == "FORBIDDEN") {
    markFailure(res, "Invalid API key");
    return;
  }

  try {
    if (resBody["link"] == null) {
      markFailure(res, "Something went wrong");
    } else {
      markSuccess(res, resBody["link"]);
    }
  } catch (err) {
    markFailure(res, "Something went wrong");
  }
}

Future<void> tinyurl (final res, String url, String token) async {
  var reqUrl = Uri.parse("https://api.tinyurl.com/create");

  var body = {
    "url": url,
  };

  var headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer $token",
  };

  var response = await http.post(reqUrl, body: json.encode(body), headers: headers);
  var resBody = json.decode(response.body);

  if (resBody["errors"].length != 0) {
    var err = resBody["errors"][0];

    if (err == "Unauthenticated.") {
      markFailure(res, "Invalid API key");
      return;
    } else if (err == "Invalid URL") {
      markFailure(res, "Invalid URL");
      return;
    }

    markFailure(res, "Something went wrong");
    return;
  }

  markSuccess(res, resBody["data"]["tiny_url"]);
}

void markSuccess (final res, final url) {
  res.json({
    'success': true,
    'url': url,
  });
}

void markFailure (final res, final message) {
  res.json({
    'success': false,
    'message': message,
  });
}

Future<void> start (final req, final res) async {

  var provider = "";
  var url = "";
  var apiKey = "";

  try {
    final payload = jsonDecode(req.payload);
    provider = payload["provider"];
    url = payload["url"];
  } catch (err) {
    markFailure(res, "Payload is invalid");
    return;
  }

  try {
    apiKey = req.variables["apiKey"];
  } catch (err) {
    markFailure(res, "apiKey is not provided");
    return;
  }

  provider = provider.toLowerCase();
  if (provider == "bitly") {
    await bitly(res, url, apiKey);
  } else if (provider == "tinyurl") {
    await tinyurl(res, url, apiKey);
  } else {
    markFailure(res, "$provider provider is not supported");
  }
}