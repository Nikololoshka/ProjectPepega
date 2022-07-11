package com.vereshchagin.nikolay.stankinschedule.news.ui.screen

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.vereshchagin.nikolay.stankinschedule.news.util.State

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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsViewerScreen(
    newsId: Int,
    viewModel: NewsViewerViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val newsContent by viewModel.newsContent.collectAsState()
    val webViewState = rememberWebViewStateWithHTMLData(
        data = (newsContent as? State.Success)?.data?.prepareQuillPage() ?: ""
    )

    val webViewAssert = remember {
        WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    LaunchedEffect(newsId) {
        viewModel.loadNewsContent(newsId)
    }

    WebView(
        state = webViewState,
        modifier = modifier,
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