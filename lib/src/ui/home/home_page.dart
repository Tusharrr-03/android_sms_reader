import 'package:android_sms/src/imports/core_imports.dart';
import 'package:android_sms/src/imports/packages_imports.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = context.theme;
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;

    return Scaffold(
      backgroundColor: colorScheme.surface,
      appBar: const AppTopBar(
        title: 'Home',
      ),
      body: SafeArea(
        child: SizedBox(
          width: double.infinity,
          child: Card(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Receiver\'s / Sender\'s Name',
                  textAlign: TextAlign.center,
                  style: textTheme.titleSmall?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onSurface,
                    fontSize: 16.sp,
                  ),
                ).center,
                SizedBox(height: AppSpacing.lg.h),
                _buildMessageCard(
                  title: 'Ref/UTR Number: ',
                  message: '1234567890',
                  titleStyle: textTheme.bodyMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onSurface,
                    fontSize: 14.sp,
                  ),
                  messageStyle: textTheme.bodyMedium?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                    fontSize: 14.sp,
                  ),
                ),
                _buildMessageCard(
                  title: 'Amount: ',
                  message: '₹ 1000',
                  titleStyle: textTheme.bodyMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onSurface,
                    fontSize: 14.sp,
                  ),
                  messageStyle: textTheme.bodyMedium?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                    fontSize: 14.sp,
                  ),
                ),
                _buildMessageCard(
                  title: 'Type: ',
                  message: 'Credit / Debit',
                  titleStyle: textTheme.bodyMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onSurface,
                    fontSize: 14.sp,
                  ),
                  messageStyle: textTheme.bodyMedium?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                    fontSize: 14.sp,
                  ),
                ),
                _buildMessageCard(
                  title: 'Account: ',
                  message: '****1234',
                  titleStyle: textTheme.bodyMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onSurface,
                    fontSize: 14.sp,
                  ),
                  messageStyle: textTheme.bodyMedium?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                    fontSize: 14.sp,
                  ),
                ),
              ],
            ).paddingSymmetric(
              horizontal: AppSpacing.sm.w,
              vertical: AppSpacing.sm.h,
            ),
          ),
        ).paddingAll(16.sp),
      ),
    );
  }

  Widget _buildMessageCard(
      {String? title,
      String? message,
      TextStyle? titleStyle,
      TextStyle? messageStyle}) {
    return Text.rich(
      TextSpan(
        text: title ?? '',
        style: titleStyle,
        children: [
          TextSpan(
            text: message ?? '',
            style: messageStyle,
          ),
        ],
      ),
    );
  }
}
