import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

Future<void> start(final req, final res) async {
  final domain = req.variables['MAILGUN_DOMAIN'];
  final apiKey = req.variables['MAILGUN_API_KEY'];

  // Validate Domain
  if ((RegExp(r"^((?!-))(xn--)?[a-z0-9][a-z0-9-_]{0,61}[a-z0-9]{0,1}\.(xn--)?([a-z0-9\-]{1,61}|[a-z0-9-]{1,30}\.[a-z]{2,})$")).hasMatch(domain) == false) {
    return res.send('Invalid domain', status: 400);
  }

  final mailgun = MailgunMailer(
    apiKey: apiKey!,
    domain: domain,
  );

  // Get the name and email of the newly created user from the payload
  final payload = jsonDecode(req.payload!);
  final name = payload['name'];
  final email = payload['email'];

  // Validate Email
  if ((RegExp(r"^[a-zA-Z0-9.a-zA-Z0-9.!#$%&'*+-/=?^_`{|}~]+@[a-zA-Z0-9]+\.[a-zA-Z]+")).hasMatch(email) == false) {
    return res.send('Invalid Email', status: 400);
  }

  // Validate Name
  if (name.length == 0) {
    return res.send('Invalid Name', status: 400);
  }

  // Create your email
  final sent = await mailgun.send(
      from: 'Welcome to My Awesome App <mailgun@$domain>',
      to: [email],
      subject: 'Welcome on board ${name}!',
      text: 'Hi ${name}\nGreat to have you with us. !');
  if (sent) {
    res.send("email sent successfully.");
  } else {
    res.send("failed to send message", status: 400);
  }
}

class MailgunMailer {
  final String domain;
  final String apiKey;

  MailgunMailer({required this.domain, required this.apiKey});

  Future<bool> send(
      {String? from,
      List<String> to = const [],
      List<String> cc = const [],
      List<String> bcc = const [],
      List<dynamic> attachments = const [],
      String? subject,
      String? html,
      String? text,
      String? template,
      Map<String, dynamic>? options}) async {
    var client = http.Client();
    try {
      var request = http.MultipartRequest(
          'POST',
          Uri(
              userInfo: 'api:$apiKey',
              scheme: 'https',
              host: 'api.mailgun.net',
              path: '/v3/$domain/messages'));
      if (subject != null) {
        request.fields['subject'] = subject;
      }
      if (html != null) {
        request.fields['html'] = html;
      }
      if (text != null) {
        request.fields['text'] = text;
      }
      if (from != null) {
        request.fields['from'] = from;
      }
      if (to.length > 0) {
        request.fields['to'] = to.join(", ");
      }
      if (cc.length > 0) {
        request.fields['cc'] = cc.join(", ");
      }
      if (bcc.length > 0) {
        request.fields['bcc'] = bcc.join(", ");
      }
      if (template != null) {
        request.fields['template'] = template;
      }
      if (options != null) {
        if (options.containsKey('template_variables')) {
          request.fields['h:X-Mailgun-Variables'] =
              jsonEncode(options['template_variables']);
        }
      }
      if (attachments.length > 0) {
        request.headers["Content-Type"] = "multipart/form-data";
        for (var i = 0; i < attachments.length; i++) {
          var attachment = attachments[i];
          if (attachment is File) {
            request.files.add(await http.MultipartFile.fromPath(
                'attachment', attachment.path));
          }
        }
      }
      var response = await client.send(request);
      if (response.statusCode != HttpStatus.ok) {
        client.close();
        throw await response.stream.bytesToString();
      }

      return true;
    } catch (e) {
      client.close();
      throw e;
    } finally {
      client.close();
    }
  }
}
