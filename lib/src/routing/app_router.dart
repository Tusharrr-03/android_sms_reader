import 'package:flutter/widgets.dart';
import 'package:go_router/go_router.dart';
import 'package:android_sms/src/middleware/app_route_observer.dart';
import 'package:android_sms/src/routing/global_navigator.dart';
import 'package:android_sms/src/routing/app_routes.dart';

import 'package:android_sms/src/ui/home/home_page.dart';
import 'package:android_sms/src/ui/onboarding/onboarding_page.dart';

import 'package:android_sms/src/shared/widgets/ui/ui_showcase_screen.dart';
import 'package:flutter/foundation.dart';

final GoRouter appRouter = GoRouter(
  navigatorKey: rootNavigatorKey,
  initialLocation: AppRoutes.home,
  observers: <NavigatorObserver>[AppRouteObserver()],
  routes: <RouteBase>[
    GoRoute(
      path: AppRoutes.onboarding,
      name: 'onboarding',
      builder: (context, state) => const OnboardingPage(),
    ),
    GoRoute(
      path: AppRoutes.home,
      name: 'home',
      builder: (context, state) => const HomePage(),
    ),
    if (kDebugMode)
      GoRoute(
        path: AppRoutes.uiShowcase,
        name: 'uiShowcase',
        builder: (context, state) => const UiShowcaseScreen(),
      ),
  ],
);
