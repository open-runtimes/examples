import 'dart:convert' as convert;

import 'package:mocktail/mocktail.dart';
import 'package:send_push_notification/fcm_service.dart';
import 'package:send_push_notification/main.dart';
import 'package:test/test.dart';


const fakeFriendDeviceToken = 'fake_friend_device_token';
const fakeServerKey = 'fake_server_key';

class Request {
  Map<String, dynamic> headers = {};
  String payload = '';
  Map<String, dynamic> variables = {
    'FMC_SERVER_KEY': fakeServerKey
  };
}

class Response {
  String responseAsString = '';
  int statusCode = 0;

  void send(text, {int status = 200}) {
    responseAsString = text;
    statusCode = status;
  }

  void json(obj, {int status = 200}) {
    responseAsString = convert.json.encode(obj);
    statusCode = status;
  }
}

class MockFCMService extends Mock implements FCMService {}

void main() {
  test('call remote function with incorrect ENV variables', () async {
    final req = Request();
    req.variables['FMC_SERVER_KEY'] = null;

    final res = Response();
    await start(req, res);
    expect(res.statusCode, 500);
    expect(res.responseAsString,
        '{"success":false,"message":"Some Environment variables are not set"}');
  });

  test('call remote function with incorrect payload', () async {
    final req = Request();
    req.payload = '';

    final res = Response();
    await start(req, res);
    expect(res.statusCode, 500);
    expect(res.responseAsString,
        '{"success":false,"message":"Payload has incorrect data, user_token is empty"}');
  });

  test('mocked services: call remote function with users.create event',
      () async {
    final req = Request();
    req.payload = '{"user_token": "$fakeFriendDeviceToken"}';

    final res = Response();
    mockFCMService = MockFCMService();

    when(() => mockFCMService!.sendFCMToUser(
          serverKey: fakeServerKey,
          userFCMToken: fakeFriendDeviceToken,
          notificationData: any(named: 'notificationData'),
        )).thenAnswer((invocation) async => true);

    await start(req, res);

    final capturedFCMDataArgs = verify(() => mockFCMService!.sendFCMToUser(
          serverKey: fakeServerKey,
          userFCMToken: captureAny(named: 'userFCMToken'),
          notificationData: captureAny(named: 'notificationData'),
        )).captured;

    expect(capturedFCMDataArgs[0], fakeFriendDeviceToken);

    final pushData = capturedFCMDataArgs[1];
    expect(pushData['Title'], 'Awesome title!');
    expect(pushData['Body'], 'Awesome body');

    expect(res.statusCode, 200);
    expect(res.responseAsString,
        '{"success":true,"message":"Push notification successfully sent"}');
  });
}
