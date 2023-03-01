import 'dart:convert';

import 'fcm_service.dart';

void returnSuccess(final res, final message) {
  res.json({
    'success': true,
    'message': message,
  }, status: 200);
}

void returnFailure(final res, final String message) {
  res.json({
    'success': false,
    'message': message,
  }, status: 500);
}

bool checkEnvVariables(final req, final res) {
  if (req.variables['APPWRITE_FUNCTION_ENDPOINT'] == null ||
      req.variables['APPWRITE_FUNCTION_API_KEY'] == null ||
      req.variables['APPWRITE_FUNCTION_PROJECT_ID'] == null ||
      req.variables['APPWRITE_FUNCTION_USER_ID'] == null ||
      req.variables['FIREBASE_AUTH_KEY'] == null) {
    returnFailure(res, "Some Environment variables are not set");
    return false;
  }
  return true;
}

bool checkPayload(Map<String, dynamic> payload, final res) {
  if (payload['user_token'] == null) {
    returnFailure(res, "Payload has incorrect data, user_token is empty");
    return false;
  }
  return true;
}

FCMService? mockFCMService;

Future<void> start(final req, final res) async {
  if (!checkEnvVariables(req, res)) {
    return;
  }
  final String firebaseAuthKey = req.variables['FIREBASE_AUTH_KEY'];

  final payload = jsonDecode(req.payload == '' ? '{}' : req.payload);
  if (!checkPayload(payload, res)) {
    return;
  }

  final String userToken = payload['user_token'];

  final fcmService = mockFCMService ?? FCMService();

  final Map<String, dynamic> notificationData = {
    'Title': "Awesome title!",
    'Body': "Awesome body",
  };

  final isPushSent = await fcmService.sendFCMToUser(
      firebaseAuthKey: firebaseAuthKey,
      userFCMToken: userToken,
      notificationData: notificationData);

  if (isPushSent) {
    returnSuccess(res, "Push notification successfully sent");
  } else {
    returnFailure(res, "Error while sending push notification");
  }
}
