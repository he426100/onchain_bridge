package com.mrtnetwork.on_chain_bridge.webview

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.webkit.WebView
import com.mrtnetwork.on_chain_bridge.OnChainCore
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

interface Callback<T> {
    fun onSuccess(data: T)
    fun onFailure(message: String)
}

@SuppressLint("SetJavaScriptEnabled")
class WebViewPlatformView(
    context: Context?,
    url: String?,
    jsInterface:String?,
   private val channel: MethodChannel,
    private val id: String
) :
    PlatformView {
    private val webView: WebView = WebView(context!!)

    // 缓存的脚本，用于在 onPageStarted 中快速注入
    var cachedInjectionScript: String? = null

    init {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = CustomWebViewClient(channel, id, this)
        webView.webChromeClient = CustomWebChromeClient(channel, id)

        if (url != null) {
            webView.loadUrl(url)
        }

    }

    override fun getView(): View {
        return webView
    }

    override fun dispose() {
        WebViewHandlers.dispose(id)
        webView.destroy()
    }
    fun addJsInterface(name: String){
        webView.addJavascriptInterface(WebAppInterface(channel,id,webView), name)

    }
    fun removeJsInterface(name:String){
        webView.removeJavascriptInterface(name)

    }
    fun injectJavaScript(jsCode: String, callback: Callback<String?>) {
        webView.evaluateJavascript(jsCode) { result ->
            if (result != null) {
                callback.onSuccess(result)
            } else {
                val errorMessage = "JavaScript evaluation failed"
                callback.onFailure(errorMessage)
            }
        }
    }

    fun openPage(url: String) {
        webView.loadUrl(url)
    }

    fun canGoForward(): Boolean {
        return webView.canGoForward()
    }

    fun canGoBack(): Boolean {
        return webView.canGoBack()
    }

    fun goBack() {
        webView.goBack()
    }

    fun goForward() {
        webView.goForward()
    }

    fun reload() {
        webView.reload()
    }
    
    fun clearCache() {
        webView.clearCache(true)
    }

    fun setCachedInjectionScript(script: String?) {
        cachedInjectionScript = script
        android.util.Log.d("OnChainBridge", "[FastInject] Cached script set, length: ${script?.length ?: 0}")
    }

    fun getWebView(): WebView {
        return webView
    }
}