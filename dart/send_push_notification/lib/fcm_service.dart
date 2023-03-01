import 'dart:convert';

import 'package:http/http.dart' as http;

class FCMService {
  Future<bool> sendFCMToUser(
      {required String firebaseAuthKey,
      required String userFCMToken,
      required Map<String, dynamic> notificationData}) async {
    try {
      await http.post(
        Uri.parse('https://fcm.googleapis.com/fcm/send'),
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
          'Authorization': 'key=$firebaseAuthKey'
        },
        body: jsonEncode(<String, dynamic>{
          'to': userFCMToken,
          'notification': notificationData
        }),
      );
    } catch (e) {
      print(e);
      return false;
    }
    return true;
  }
}
