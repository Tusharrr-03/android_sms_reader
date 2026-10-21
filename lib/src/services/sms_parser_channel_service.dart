import 'package:flutter/services.dart';

class SmsParserChannelService {
  SmsParserChannelService({MethodChannel? channel})
      : _channel = channel ?? const MethodChannel(channelName);

  static const channelName = 'com.example.android_sms_reader/sms_parser';
  static const contractVersion = 1;

  final MethodChannel _channel;

  Future<int> getContractVersion() async {
    final version = await _channel.invokeMethod<int>('getContractVersion');
    if (version == null) {
      throw const FormatException('Missing SMS parser contract version');
    }
    return version;
  }

  Future<SmsParseResult> parseSms({
    required String sender,
    required String body,
    int? receivedAtMillis,
  }) async {
    final value = await _channel.invokeMethod<Object?>('parseSms', {
      'sender': sender,
      'body': body,
      if (receivedAtMillis != null) 'receivedAtMillis': receivedAtMillis,
    });
    if (value is! Map) {
      throw const FormatException('Invalid SMS parser result');
    }
    return SmsParseResult.fromMap(Map<Object?, Object?>.from(value));
  }
}

class SmsParseResult {
  const SmsParseResult({
    required this.schemaVersion,
    required this.status,
    required this.messageType,
    required this.score,
    required this.reasons,
    required this.originalBody,
    required this.transaction,
  });

  final int schemaVersion;
  final String status;
  final String messageType;
  final int score;
  final List<String> reasons;
  final String originalBody;
  final SmsTransactionData? transaction;

  factory SmsParseResult.fromMap(Map<Object?, Object?> value) {
    final reasonsValue = value['reasons'];
    final transactionValue = value['transaction'];
    if (value['schemaVersion'] is! int ||
        value['status'] is! String ||
        value['messageType'] is! String ||
        value['score'] is! int ||
        value['originalBody'] is! String ||
        reasonsValue is! List ||
        reasonsValue.any((reason) => reason is! String) ||
        (transactionValue != null && transactionValue is! Map)) {
      throw const FormatException('Malformed SMS parser result');
    }

    return SmsParseResult(
      schemaVersion: value['schemaVersion']! as int,
      status: value['status']! as String,
      messageType: value['messageType']! as String,
      score: value['score']! as int,
      reasons: List<String>.from(reasonsValue),
      originalBody: value['originalBody']! as String,
      transaction: transactionValue == null
          ? null
          : SmsTransactionData.fromMap(
              Map<Object?, Object?>.from(transactionValue as Map),
            ),
    );
  }
}

class SmsTransactionData {
  const SmsTransactionData({
    required this.amount,
    required this.direction,
    required this.referenceNumber,
    required this.accountSuffix,
    required this.transactionDateTime,
  });

  final double? amount;
  final String direction;
  final String? referenceNumber;
  final String? accountSuffix;
  final String? transactionDateTime;

  factory SmsTransactionData.fromMap(Map<Object?, Object?> value) {
    final amountValue = value['amount'];
    if ((amountValue != null && amountValue is! num) ||
        value['direction'] is! String ||
        (value['referenceNumber'] != null &&
            value['referenceNumber'] is! String) ||
        (value['accountSuffix'] != null && value['accountSuffix'] is! String) ||
        (value['transactionDateTime'] != null &&
            value['transactionDateTime'] is! String)) {
      throw const FormatException('Malformed SMS transaction data');
    }
    return SmsTransactionData(
      amount: (amountValue as num?)?.toDouble(),
      direction: value['direction']! as String,
      referenceNumber: value['referenceNumber'] as String?,
      accountSuffix: value['accountSuffix'] as String?,
      transactionDateTime: value['transactionDateTime'] as String?,
    );
  }
}
