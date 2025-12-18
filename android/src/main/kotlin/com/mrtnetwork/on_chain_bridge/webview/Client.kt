package com.mrtnetwork.on_chain_bridge.webview

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mrtnetwork.on_chain_bridge.OnChainCore
import io.flutter.plugin.common.MethodChannel

class CustomWebViewClient(
    private val methodChannel: MethodChannel,
    val id: String,
    private val platformView: WebViewPlatformView
) : WebViewClient() {

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        // 关键优化：立即注入缓存的脚本（如果有）
        val cachedScript = platformView.cachedInjectionScript
        if (cachedScript != null) {
            view.evaluateJavascript(cachedScript, null)
            android.util.Log.d("OnChainBridge", "[FastInject] Cached script injected in onPageStarted")

            // 检测 window.ethereum 何时可用
            view.postDelayed({
                view.evaluateJavascript("(function() { return typeof window.ethereum !== 'undefined'; })()") { result ->
                    android.util.Log.d("OnChainBridge", "[FastInject] window.ethereum available after 50ms: $result")
                }
            }, 50)

            view.postDelayed({
                view.evaluateJavascript("(function() { return typeof window.ethereum !== 'undefined'; })()") { result ->
                    android.util.Log.d("OnChainBridge", "[FastInject] window.ethereum available after 100ms: $result")
                }
            }, 100)

            view.postDelayed({
                view.evaluateJavascript("(function() { return typeof window.ethereum !== 'undefined'; })()") { result ->
                    android.util.Log.d("OnChainBridge", "[FastInject] window.ethereum available after 200ms: $result")
                }
            }, 200)
        }

        view.evaluateJavascript(  "(function() {" +
                "var links = document.getElementsByTagName('link');" +
                "for (var i = 0; i < links.length; i++) {" +
                "    if (links[i].rel.includes('icon')) {" +
                "        return links[i].href;" +
                "    }" +
                "}" +
                "for (var i = 0; i < links.length; i++) {" +
                "    if (links[i].rel.includes('shortcut icon')) {" +
                "        return links[i].href;" +
                "    }" +
                "}" +
                "return null;" +
                "})()") { result ->
            if (result != null) {
                val data = WebViewUtils.toJson(id,WebViewConst.onPageStart,view, url = url, favicon = result)
                methodChannel.invokeMethod(WebViewConst.webView, data.toJson())
            } else {
                val data = WebViewUtils.toJson(id,WebViewConst.onPageStart,view, url = url)
                methodChannel.invokeMethod(WebViewConst.webView, data.toJson())
            }
        }
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        val data = WebViewUtils.toJson(id,WebViewConst.onPageFinished,view, url = url)
        methodChannel.invokeMethod(WebViewConst.webView, data.toJson())
    }

    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        super.onReceivedError(view, request, error)
        val errorMessage =error.description.toString()
        val data = WebViewUtils.toJson(id,WebViewConst.onPageError,view, message = errorMessage)
        methodChannel.invokeMethod(WebViewConst.webView, data.toJson())
    }

}

class CustomWebChromeClient(private val methodChannel: MethodChannel, val id: String) : WebChromeClient() {
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        val data = WebViewUtils.toJson(id,WebViewConst.onPageProgress,view, progress = newProgress)
        methodChannel.invokeMethod(WebViewConst.webView, data.toJson())
    }

}