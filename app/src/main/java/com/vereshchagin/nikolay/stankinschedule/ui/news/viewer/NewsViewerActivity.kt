package com.vereshchagin.nikolay.stankinschedule.ui.news.viewer

import android.annotation.SuppressLint
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
import androidx.webkit.WebViewFeature
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityNewsViewerBinding
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPost
import com.vereshchagin.nikolay.stankinschedule.utils.*
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
            .init(StatefulLayout2.LOADING, binding.newsLoading.loadingFragment)
            .addView(StatefulLayout2.ERROR, binding.newsError.errorButtonFragment)
            .addView(StatefulLayout2.CONTENT, binding.newsView)
            .create()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // номер новости
        val newsId = intent.getIntExtra(NEWS_ID, -1)
        if (newsId == -1) {
            throw RuntimeException("NewsID is null: $newsId")
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
        binding.newsView.settings.apply {
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
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
                    Toast.makeText(this@NewsViewerActivity, message(), Toast.LENGTH_LONG).show()
                }
                return true
            }
        }

        // переадресация ссылок
        binding.newsView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                CommonUtils.openBrowser(this@NewsViewerActivity, request?.url.toString())
                return true
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

        // обновление по свайпу
        binding.newsRefresh.setOnRefreshListener {
            viewModel.refresh(false)
        }

        // обновление после ошибки
        binding.newsError.errorAction.setOnClickListener {
            viewModel.refresh()
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
                    null,
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
                binding.newsError.errorTitle.text = state.error.toString()
                stateful.setState(StatefulLayout2.ERROR)
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

        const val NEWS_ID = "news_id"

        /**
         * Создает Bundle для создания фрагмента.
         * @param newsId номер новости.
         */
        fun createBundle(newsId: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt(NEWS_ID, newsId)
            return bundle
        }
    }
}