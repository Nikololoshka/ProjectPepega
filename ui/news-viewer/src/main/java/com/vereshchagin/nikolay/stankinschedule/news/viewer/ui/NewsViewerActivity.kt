package com.vereshchagin.nikolay.stankinschedule.news.viewer.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.webkit.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewFeature
import coil.load
import com.vereshchagin.nikolay.stankinschedule.core.domain.ext.formatDate
import com.vereshchagin.nikolay.stankinschedule.core.domain.logger.LoggerAnalytics
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.UIState
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.setVisibility
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.exceptionDescription
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.newsImageLoader
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsContent
import com.vereshchagin.nikolay.stankinschedule.news.viewer.ui.databinding.ActivityNewsViewerBinding
import com.vereshchagin.nikolay.stankinschedule.news.viewer.utils.NewsBrowserUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewsViewerActivity : AppCompatActivity() {

    @Inject
    lateinit var loggerAnalytics: LoggerAnalytics

    private val viewModel: NewsViewerViewModel by viewModels()

    private lateinit var binding: ActivityNewsViewerBinding

    private val imageLoader by lazy { newsImageLoader(this) }
    private var newsId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Аргументы
        val newsTitle = intent.getStringExtra(NEWS_TITLE)
        newsId = intent.getIntExtra(NEWS_ID, -1)

        binding.toolbar.title = newsTitle
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClickListener)

        binding.newsRefresh.setOnRefreshListener { viewModel.loadNewsContent(newsId, force = true) }
        binding.errorAction.setOnClickListener { viewModel.loadNewsContent(newsId) }

        setupWebViewSettings()
        viewModel.loadNewsContent(newsId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsContent.collect { state ->
                    if (state is UIState.Success) {
                        updateContent(state.data)
                    }
                    if (state is UIState.Failed) {
                        val description = exceptionDescription(state.error)
                        binding.errorTitle.text = description ?: state.error.toString()
                    }

                    updateVisibleView(state)
                }
            }
        }

        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_ENTER, "NewsViewerActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        loggerAnalytics.logEvent(LoggerAnalytics.SCREEN_LEAVE, "NewsViewerActivity")
    }

    private fun onMenuItemClickListener(item: MenuItem): Boolean {
        when (item.itemId) {
            // Открыть в браузере
            R.id.open_in_browser -> {
                openLink(NewsBrowserUtils.linkForPost(newsId))
                return true
            }
            // Поделится
            R.id.news_share -> {
                val url = NewsBrowserUtils.linkForPost(newsId)
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                return true
            }
            // Обновить
            R.id.news_update -> {
                viewModel.loadNewsContent(newsId, force = true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupWebViewSettings() {
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        binding.newsView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                supportDarkMode(isDarkTheme = isDarkMode())
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

        // переадресация ссылок
        binding.newsView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                openLink(request?.url.toString())
                return true
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest,
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }
    }

    private fun openLink(url: String) {
        BrowserUtils.openLink(this, url)
    }

    private fun newsBackgroundHex(): String {
        val backgroundColor = resources.getColor(R.color.news_viewer_background, theme)
        return "#" + Integer.toHexString(backgroundColor).drop(2)
    }

    private fun updateContent(content: NewsContent) {
        binding.newsPreview.load(content.previewImageUrl, imageLoader)
        binding.toolbar.title = content.title
        binding.newsDate.text = formatDate(content.date)

        binding.newsView.loadDataWithBaseURL(
            null,
            content.prepareQuillPage(newsBackgroundHex()),
            "text/html; charset=UTF-8",
            "UTF-8",
            null
        )
    }

    private fun updateVisibleView(state: UIState<*>) {
        binding.newsView.setVisibility(state is UIState.Success)
        binding.newsRefresh.isRefreshing = state is UIState.Loading
        binding.newsError.setVisibility(state is UIState.Failed)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun WebView.supportDarkMode(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            // Old API
            // WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
            if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, true)
            }
        }
    }

    private fun isDarkMode(): Boolean {
        val currentNightMode = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
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

    companion object {

        private const val NEWS_TITLE = "news_title"
        private const val NEWS_ID = "news_id"

        fun createIntent(context: Context, newsTitle: String?, newsId: Int): Intent {
            return Intent(context, NewsViewerActivity::class.java).apply {
                putExtra(NEWS_TITLE, newsTitle)
                putExtra(NEWS_ID, newsId)
            }
        }
    }
}