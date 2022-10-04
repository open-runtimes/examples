import 'dart:convert';
import 'package:generate_short_url/utils/api.dart';
import 'package:http/http.dart' as http;

Future<void> bitly (final res, String url) async {
  String token = ApiInfo.bitly_token;
  String group_guid = ApiInfo.bitly_group_guid;
  var reqUrl = Uri.parse(ApiInfo.bitly_url);
  var body = {
    "group_guid": group_guid,
    "long_url": url,
  };
  var headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer $token",
  };
  var response = await http.post(reqUrl, body: json.encode(body), headers: headers);
  try {
    if (json.decode(response.body)["link"] == null) {
      markFailure(res, "Invalid URL");
    } else {
      markSuccess(res, json.decode(response.body)["link"]);
    }
  } catch (err) {
    markFailure(res, "Invalid URL");
  }
}

Future<void> tinyurl (final res, String url) async {
  String token = ApiInfo.tinyurl_token;
  var reqUrl = Uri.parse(ApiInfo.tinyurl_url);
  var body = {
    "apikey": token,
    "url": url,
  };
  var headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer $token",
  };
  var response = await http.post(reqUrl, body: json.encode(body), headers: headers);
  try {
    markSuccess(res, json.decode(response.body)["data"]["tiny_url"]);
  } catch (err) {
    markFailure(res, "Invalid URL");
  }
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
  try {
    final payload = jsonDecode(req.payload);
    provider = payload["provider"];
    url = payload["url"];
  } catch (err) {
    print(err);
    markFailure(res, "Payload is invalid");
    return;
  }

  provider = provider.toLowerCase();
  if (provider == "bitly") {
    await bitly(res, url);
  } else if (provider == "tinyurl") {
    await tinyurl(res, url);
  } else {
    markFailure(res, "Provider is not supported");
  }
}