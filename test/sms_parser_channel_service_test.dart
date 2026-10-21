import 'package:android_sms/src/services/sms_parser_channel_service.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const channel = MethodChannel(SmsParserChannelService.channelName);
  final service = SmsParserChannelService(channel: channel);

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('reads the contract version', () async {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async => 1);
    expect(await service.getContractVersion(), 1);
  });

  test('sends versioned parse arguments and decodes the result', () async {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async {
      expect(call.method, 'parseSms');
      expect(call.arguments, {
        'sender': 'AB-HDFCBK',
        'body': 'INR 500 debited',
        'receivedAtMillis': 42,
      });
      return {
        'schemaVersion': 1,
        'status': 'AUTO_ACCEPTED',
        'messageType': 'TRANSACTION',
        'score': 80,
        'reasons': <String>[],
        'originalBody': 'INR 500 debited',
        'transaction': {
          'amount': 500.0,
          'direction': 'DEBIT',
          'referenceNumber': null,
          'accountSuffix': null,
          'transactionDateTime': null,
        },
      };
    });

    final parsed = await service.parseSms(
      sender: 'AB-HDFCBK',
      body: 'INR 500 debited',
      receivedAtMillis: 42,
    );
    expect(parsed.schemaVersion, 1);
    expect(parsed.transaction?.amount, 500.0);
    expect(parsed.transaction?.direction, 'DEBIT');
  });

  test('rejects malformed native results', () async {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async => {'test': 'bad'});
    expect(
      () => service.parseSms(sender: 'AB-HDFCBK', body: 'message'),
      throwsFormatException,
    );
  });
}
