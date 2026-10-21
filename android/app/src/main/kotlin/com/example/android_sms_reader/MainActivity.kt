package com.example.android_sms_reader

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import com.example.android_sms_reader.parser.BankSmsParser
import com.example.android_sms_reader.parser.SmsInput

class MainActivity : FlutterActivity() {
	override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
		super.configureFlutterEngine(flutterEngine)
		MethodChannel(
			flutterEngine.dartExecutor.binaryMessenger,
			SMS_PARSER_CHANNEL
		).setMethodCallHandler { call, result ->
			if (!BuildConfig.SMS_PARSER_ENABLED) {
				result.notImplemented()
				return@setMethodCallHandler
			}

			when (call.method) {
				"getContractVersion" -> result.success(SMS_PARSER_CONTRACT_VERSION)
				"parseSms" -> {
					val arguments = call.arguments as? Map<*, *>
					val sender = arguments?.get("sender") as? String
					val body = arguments?.get("body") as? String
					val receivedAtMillisValue = arguments?.get("receivedAtMillis")
					val receivedAtMillis = when (receivedAtMillisValue) {
						null -> null
						is Number -> receivedAtMillisValue.toLong()
						else -> null
					}

					if (sender == null || body == null ||
						(receivedAtMillisValue != null && receivedAtMillis == null)
					) {
						result.error(
							"INVALID_ARGUMENTS",
							"parseSms requires String sender/body and an optional numeric receivedAtMillis",
							null
						)
						return@setMethodCallHandler
					}

					try {
						val parsed = BankSmsParser().parse(
							SmsInput(sender, body, receivedAtMillis)
						)
						result.success(
							mapOf(
								"schemaVersion" to SMS_PARSER_CONTRACT_VERSION,
								"status" to parsed.status.name,
								"messageType" to parsed.messageType.name,
								"score" to parsed.score,
								"reasons" to parsed.reasons,
								"originalBody" to parsed.originalBody,
								"transaction" to parsed.transaction?.let { transaction ->
									mapOf(
										"amount" to transaction.amount,
										"direction" to transaction.direction.name,
										"referenceNumber" to transaction.referenceNumber,
										"accountSuffix" to transaction.accountSuffix,
										"transactionDateTime" to transaction.transactionDateTime
									)
								}
							)
						)
					} catch (_: Exception) {
						result.error("PARSER_ERROR", "Unable to parse SMS message", null)
					}
				}
				else -> result.notImplemented()
			}
		}
	}

	private companion object {
		const val SMS_PARSER_CHANNEL = "com.example.android_sms_reader/sms_parser"
		const val SMS_PARSER_CONTRACT_VERSION = 1
	}
}
