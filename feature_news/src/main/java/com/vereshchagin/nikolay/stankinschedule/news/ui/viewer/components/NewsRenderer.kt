package com.vereshchagin.nikolay.stankinschedule.news.ui.viewer.components

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.vereshchagin.nikolay.stankinschedule.news.domain.model.NewsContent


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsRenderer(
    content: NewsContent,
    onRedirect: (uri: Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val webViewState = rememberWebViewStateWithHTMLData(data = content.prepareQuillPage())

    val webViewAssert = remember {
        WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    Box(
        modifier = modifier
    ) {
        WebView(
            state = webViewState,
            modifier = Modifier.fillMaxSize(),
            captureBackPresses = false,
            onCreated = { webView ->
                webView.apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        supportDarkMode(isDarkTheme = isDarkTheme)
                    }

                    settings.apply {
                        allowFileAccess = true
                        loadsImagesAutomatically = true
                        javaScriptEnabled = true // Suppressed!!!
                    }

                    addJavascriptInterface(NewsViewInterface {
                        // Callback from js
                    }, "Android")
                }
            },
            client = object : AccompanistWebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): Boolean {
                    // opening link
                    if (request != null) onRedirect(request.url)
                    return true
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest,
                ): WebResourceResponse? {
                    return webViewAssert.shouldInterceptRequest(request.url)
                }
            },
            chromeClient = object : AccompanistWebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    // webview console
                    return super.onConsoleMessage(consoleMessage)
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun WebView.supportDarkMode(isDarkTheme: Boolean) {
    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && isDarkTheme) {
        WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
    }
}

/**
 * Интерфейс для определения загрузки HTML в WebView.
 */
class NewsViewInterface(private val loaded: () -> Unit) {
    @JavascriptInterface
    fun onNewsLoaded() {
        loaded.invoke()
    }
}