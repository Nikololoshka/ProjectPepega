package com.vereshchagin.nikolay.stankinschedule.ui.news.viewer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.material.appbar.AppBarLayout
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentNewsViewerBinding
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.formatDate
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.parseDate
import com.vereshchagin.nikolay.stankinschedule.utils.LoadState
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2

/**
 * Фрагмент для просмотра новости.
 */
class NewsViewerFragment : Fragment() {

    private var _binding: FragmentNewsViewerBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel фрагмента.
     */
    private lateinit var viewModel: NewsViewerViewModel

    /**
     * Glide для загрузки фото новости.
     */
    private lateinit var glide: RequestManager

    /**
     * StatefulLayout для отображения разных View
     */
    private var _stateful: StatefulLayout2? = null
    private val stateful get() = _stateful!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsViewerBinding.inflate(inflater, container, false)
        _stateful = StatefulLayout2(binding.root, StatefulLayout2.LOADING, binding.newsLoading.loadingFragment)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateful.addView(StatefulLayout2.ERROR, binding.newsError.errorButtonFragment)
        stateful.addView(StatefulLayout2.CONTENT, binding.newsRefresh)

        // поддержка темной темы
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val currentNightMode = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    WebSettingsCompat.setForceDark(binding.newsView.settings, WebSettingsCompat.FORCE_DARK_ON)
                    context?.let {
                        binding.newsLayout.setBackgroundColor(ContextCompat.getColor(it, R.color.colorNewsBackgroundDark))
                    }
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

        binding.newsView.addJavascriptInterface(NewsViewInterface {
            val loadState = viewModel.state.value
            showView(loadState)
        }, "Android")

        binding.newsView.isVerticalScrollBarEnabled = false

        binding.newsView.webChromeClient = object  : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.apply {
                    Toast.makeText(requireContext(), message(), Toast.LENGTH_LONG).show()
                }
                return true
            }
        }
        binding.newsView.webViewClient = object : WebViewClient() {
            // переадресация ссылок
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val ctx = context
                if (ctx != null) {
                    CommonUtils.openBrowser(ctx, request?.url.toString())
                    return true
                }
                return false
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_news_viewer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // открыть новость в браузере
            R.id.open_in_browser -> {
                val url = "https://stankin.ru/news/item_${viewModel.newsId}"
                context?.let { CommonUtils.openBrowser(it, url) }
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // номер новости
        val newsId = arguments?.getInt(NEWS_ID)
        viewModel = ViewModelProvider(this,
            NewsViewerViewModel.Factory(newsId!!, activity?.application!!))
            .get(NewsViewerViewModel::class.java)

        // обновление по свайпу
        binding.newsRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        // обновление после ошибки
        binding.newsError.errorAction.setOnClickListener {
            viewModel.refresh()
        }

        viewModel.state.observe(viewLifecycleOwner, Observer { loadState ->
            if (loadState.state == LoadState.State.SUCCESS) {
                val post = viewModel.post()

                if (post == null) {
                    showView(LoadState.error(getString(R.string.news_unknown_error)))
                    return@Observer
                }

                val shimmerDrawable = ShimmerDrawable().apply {
                    setShimmer(Shimmer.AlphaHighlightBuilder()
                        .setDuration(2000)
                        .setBaseAlpha(0.7f)
                        .setHighlightAlpha(0.6f)
                        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                        .setAutoStart(true)
                        .build())
                }

                glide.load(post.logoUrl())
                    .placeholder(shimmerDrawable)
                    .centerCrop()
                    .into(binding.newsPreview)

                binding.newsView.loadDataWithBaseURL(
                    null,
                    post.quillPage(),
                    "text/html; charset=UTF-8",
                    "UTF-8",
                    null
                )

                binding.newsTitle.text = post.title
                binding.newsDate.text = parseDate(post.onlyDate())?.let { formatDate(it) }
            }
            showView(loadState)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _stateful = null
    }

    /**
     * Показывает необходимую View.
     * @param loadState статус загрузки.
     */
    private fun showView(loadState: LoadState?) {
        if (loadState?.state != LoadState.State.RUNNING) {
            binding.newsRefresh.isRefreshing = false
        }

        when (loadState?.state) {
            LoadState.State.RUNNING -> stateful.setState(StatefulLayout2.LOADING)
            LoadState.State.SUCCESS -> stateful.setState(StatefulLayout2.CONTENT)
            else -> {
                binding.newsError.errorTitle.text = loadState?.msg
                stateful.setState(StatefulLayout2.ERROR)
            }
        }
    }

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
