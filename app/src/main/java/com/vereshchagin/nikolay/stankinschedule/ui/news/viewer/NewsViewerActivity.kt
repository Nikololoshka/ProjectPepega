package com.vereshchagin.nikolay.stankinschedule.ui.news.viewer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewFeature
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityNewsViewerBinding
import com.vereshchagin.nikolay.stankinschedule.databinding.ViewErrorWithButtonBinding
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPost
import com.vereshchagin.nikolay.stankinschedule.utils.*
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.createBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Активность для просмотра новостей.
 */
class NewsViewerActivity : AppCompatActivity() {

    /**
     * ViewModel активности.
     */
    private lateinit var viewModel: NewsViewerViewModel

    /**
     * Glide для загрузки фото новости.
     */
    private lateinit var glide: RequestManager

    private lateinit var stateful: StatefulLayout2
    private lateinit var binding: ActivityNewsViewerBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsViewerBinding.inflate(layoutInflater)
        stateful = StatefulLayout2.Builder(binding.newsLayout)
            .init(StatefulLayout2.LOADING, binding.newsLoading.root)
            .addView(StatefulLayout2.ERROR, binding.newsError)
            .addView(StatefulLayout2.CONTENT, binding.newsView)
            .create()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // номер новости
        var newsId = -1

        // напрямую вызов просмотра
        if (intent.action == Intent.ACTION_VIEW) {
            val path = intent.data?.path // /news/item_{news_id}
            val newsIdData = path?.substringAfterLast("item_", "-1")
            val maybeNewsId = newsIdData?.toIntOrNull()
            if (maybeNewsId != null) {
                newsId = maybeNewsId
            }
        } else {
            // из приложения
            newsId = intent.getIntExtra(EXTRA_NEWS_ID, -1)
        }

        if (newsId <= -1) {
            Toast.makeText(this, "Invalid news id: $newsId", Toast.LENGTH_SHORT).show()
            onBackPressed()
            return
        }

        // название новости
        val newsTitle = intent.getStringExtra(EXTRA_NEWS_TITLE)
        if (newsTitle != null) {
            binding.toolbarLayout.title = newsTitle
        }

        viewModel = ViewModelProvider(
            this, NewsViewerViewModel.Factory(newsId, application)
        ).get(NewsViewerViewModel::class.java)

        // поддержка темной темы
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val currentNightMode = resources.configuration.uiMode.and(
                    Configuration.UI_MODE_NIGHT_MASK
                )
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    WebSettingsCompat.setForceDark(
                        binding.newsView.settings,
                        WebSettingsCompat.FORCE_DARK_ON
                    )
                    binding.newsLayout.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.colorNewsBackgroundDark)
                    )
                }
            }
        }

        // настройка WebView
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        binding.newsView.settings.apply {
            allowFileAccess = true
            loadsImagesAutomatically = true
            javaScriptEnabled = true
        }

        // добавление интерфейса к исполнению JavaScript
        binding.newsView.addJavascriptInterface(NewsViewInterface {
            // обновляем в UI потоке
            lifecycleScope.launch(Dispatchers.Main) {
                showProgressView(viewModel.post.value)
            }
        }, "Android")

        binding.newsView.isVerticalScrollBarEnabled = false

        // лог WebView
        binding.newsView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.apply {
                    if (messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                        Toast.makeText(
                            this@NewsViewerActivity,
                            message().take(80) + Typography.ellipsis,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Firebase.crashlytics.recordException(IllegalArgumentException(message()))
                    }
                }
                return true
            }
        }

        // переадресация ссылок
        binding.newsView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                CommonUtils.openBrowser(this@NewsViewerActivity, request?.url.toString())
                return true
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest,
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        binding.appBarPost.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.newsRefresh.isEnabled = verticalOffset == 0
            }
        )

        glide = Glide.with(this)
            .setDefaultRequestOptions(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
            )

        // обновление новости
        binding.newsRefresh.setOnRefreshListener {
            viewModel.refresh(false)
        }

        // пост
        viewModel.post.observe(this, Observer {
            val state = it ?: return@Observer

            // успешно загружен
            if (state is State.Success) {
                glide.load(state.data.logoUrl())
                    .placeholder(DrawableUtils.createShimmerDrawable())
                    .centerCrop()
                    .into(binding.newsPreview)

                binding.newsView.loadDataWithBaseURL(
                    "file:///android_asset/news/filename.html",
                    state.data.quillPage(),
                    "text/html; charset=UTF-8",
                    "UTF-8",
                    null
                )

                binding.toolbarLayout.title = state.data.title
                binding.newsDate.text = getString(
                    R.string.news_post_date,
                    DateUtils.parseDate(state.data.onlyDate())?.let { date ->
                        DateUtils.formatDate(date)
                    }
                )

            } else {
                showProgressView(state)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_news_viewer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // открыть новость в браузере
            R.id.open_in_browser -> {
                val url = "https://stankin.ru/news/item_${viewModel.newsId}"
                CommonUtils.openBrowser(this, url)
                return true
            }
            // поделиться новостью
            R.id.news_share -> {
                val url = "https://stankin.ru/news/item_${viewModel.newsId}"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                return true
            }
            // обновить новость ->
            R.id.news_update -> {
                viewModel.refresh()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Показывает необходимую View в зависимости от статуса.
     * @param state статус загрузки.
     */
    @MainThread
    private fun showProgressView(state: State<NewsPost>?) {
        when (state) {
            is State.Success -> {
                stateful.setState(StatefulLayout2.CONTENT)
            }
            is State.Loading -> {
                stateful.setState(StatefulLayout2.LOADING)
            }
            is State.Failed -> {
                val description = ExceptionUtils.errorDescription(state.error, this)

                binding.newsError.createBinding<ViewErrorWithButtonBinding>()?.let {
                    it.errorTitle.text = description
                    it.errorAction.setOnClickListener {
                        viewModel.refresh()
                    }

                    stateful.setState(StatefulLayout2.ERROR)
                }
            }
        }

        if (state !is State.Loading) {
            binding.newsRefresh.isRefreshing = false
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

    companion object {

        private const val TAG = "NewsViewerActivityLog"

        private const val EXTRA_NEWS_ID = "news_id"
        private const val EXTRA_NEWS_TITLE = "extra_news_title"

        /**
         *  Возвращает Intent на просмотр новости.
         */
        fun newsIntent(context: Context, newsId: Int, newsTitle: String?): Intent {
            val intent = Intent(context, NewsViewerActivity::class.java)
            intent.putExtra(EXTRA_NEWS_ID, newsId)
            intent.putExtra(EXTRA_NEWS_TITLE, newsTitle)
            return intent
        }
    }
}