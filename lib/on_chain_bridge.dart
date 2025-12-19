// This Flutter plugin incorporates code inspired by the following projects:

// 1. [flutter_secure_storage](https://pub.dev/packages/flutter_secure_storage) - Some of the methods in this plugin are inspired by the flutter_secure_storage plugin, which is licensed under the BSD license. The original project can be found at: https://github.com/mogol/flutter_secure_storage
// 2. [window_manager](https://pub.dev/packages/window_manager) - Additionally, some methods are inspired by the window_manager plugin, which is licensed under the MIT license. The original project can be found at: https://github.com/leanflutter/window_manager

import 'dart:async';
import 'package:on_chain_bridge/database/database.dart';
import 'package:on_chain_bridge/models/biometric/types.dart';
import 'package:on_chain_bridge/models/models.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

abstract class OnChainBridgeInterface<
        CREDENTIALRESPONSE extends PlatformCredentialResponse,
        CREDENTIALAUTHREQUEST extends PlatformCredentialAutneticateRequest>
    extends PlatformInterface {
  OnChainBridgeInterface() : super(token: _token);
  static final Object _token = Object();
  abstract final SpecificPlatfromMethods desktop;
  abstract final PlatformWebView webView;
  AppPlatform get platform;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PlatformInterface] when
  /// they register themselves.
  static set instance(OnChainBridgeInterface instance) {
    PlatformInterface.verifyToken(instance, _token);
  }

  static void registerWith() {}

  Future<bool> secureFlag({required bool isSecure});

  Future<bool> share(Share share);
  Future<AppPath> path(String applicationId);
  Future<DeviceInfo> getDeviceInfo();
  Future<bool> launchUri(String uri);
  Future<Stream<BarcodeScannerResult>> startBarcodeScanner(
      {required BarcodeScannerParams param});
  Future<void> stopBarcodeScanner();
  Future<bool> hasBarcodeScanner();
  Future<PlatformConfig> init(String applicationId);
  Future<String?> readClipboard();
  Future<bool> writeClipboard(String text);
  Stream<bool> get onNetworkStatus;

  Future<DATA?> readDb<DATA extends ITableData>(ITableRead<DATA> params);
  Future<bool> removeDb(ITableRemove params);
  Future<bool> writeDb(ITableInsertOrUpdate params);
  Future<List<DATA>> readAllDb<DATA extends ITableData>(
      ITableRead<DATA> params);
  Future<bool> writeAllDb(List<ITableInsertOrUpdate> params);
  Future<bool> removeAllDb(List<ITableRemove> params);
  Future<bool> dropDb(ITableDrop params);

  /// biometric
  Future<TouchIdStatus> touchIdStatus();
  Future<BiometricResult> authenticate(CREDENTIALAUTHREQUEST request);

  Future<CREDENTIALRESPONSE?> createPlatformCredential(
      PlatformCredentialRequest params);

  ///
  Future<PickedFileContent?> pickAndReadFileContent(
      {PickFileContentEncoding encoding = PickFileContentEncoding.hex,
      AppFileType? type = AppFileType.txt});
  Future<bool> saveFile(
      {required String filePath,
      required String fileName,
      String? title,
      AppFileType type = AppFileType.txt});
}

abstract class PlatformWebView {
  bool get supported;
  Future<Object?> loadScript(
      {required String viewType, required String script});
  Future<void> openUrl({required String viewType, required String url});
  Future<bool> canGoForward(String viewType);
  Future<bool> canGoBack(String viewType);
  Future<void> goBack(String viewType);
  Future<void> goForward(String viewType);
  Future<void> reload(String viewType);
  Future<void> dispose(String viewType);
  Future<void> addJsInterface({required String viewType, required String name});
  Future<void> removeJsInterface(
      {required String viewType, required String name});
  Future<void> updateFrame(
      {required String viewType, required WidgetSize size});
  Future<void> init(String viewType,
      {String url = "https://google.com", String? jsInterface = "onChain"});
  Future<void> clearCache(String viewType);
  void addListener(WebViewListener listener);
  void removeListener(WebViewListener listener);
}

abstract class SpecificPlatfromMethods {
  Future<bool> show();
  Future<bool> hide();
  Future<bool> init();
  Future<bool> setIcon(String path);
  Future<bool> setMaximumSize(WidgetSize size);
  Future<bool> setMinimumSize(WidgetSize size);
  Future<void> setBounds(
      {required double pixelRatio,
      WidgetRect? bounds,
      WidgetOffset? position,
      WidgetSize? size,
      bool animate = false});
  Future<bool> setPreventClose(bool preventClose);

  Future<bool> setFullScreen(bool isFullScreen);
  Future<bool> isFullScreen();
  Future<bool> restore();
  Future<bool> minimize();
  Future<bool> isMinimized();
  Future<bool> unmaximize();
  Future<bool> isMaximized();
  Future<bool> isVisible();
  Future<void> waitUntilReadyToShow();
  Future<bool> isFocused();
  Future<bool> blur();
  Future<bool> focus();
  Future<bool> isPreventClose();
  Future<bool> close();
  Future<bool> setAsFrameless();
  Future<bool> isResizable();
  Future<bool> setResizable(bool isResizable);
  Future<WidgetRect> getBounds(double pixelRatio);

  void addListener(WindowListener listener);

  void removeListener(WindowListener listener);
}
