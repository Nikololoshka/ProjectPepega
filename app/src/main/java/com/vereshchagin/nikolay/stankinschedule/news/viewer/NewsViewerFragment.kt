package com.vereshchagin.nikolay.stankinschedule.news.viewer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentNewsViewerBinding
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.formatDate
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.parseDate
import com.vereshchagin.nikolay.stankinschedule.utils.LoadState

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
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.newsView.isVerticalScrollBarEnabled = false

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
            // страница загружена
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                val loadState = viewModel.state.value
                showView(loadState)
            }
        }

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
            // подельться новостью
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // номер новости
        val newsId = arguments?.getInt(NEWS_ID)
        viewModel = ViewModelProviders.of(this,
            NewsViewerViewModel.Factory(newsId!!, activity?.application!!))
            .get(NewsViewerViewModel::class.java)

        viewModel.state.observe(viewLifecycleOwner, Observer { loadState ->
            if (loadState.state == LoadState.State.SUCCESS) {
                val post = viewModel.post()
                if (post != null) {
                    glide.load(post.logoUrl())
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
                    binding.newsDate.text = formatDate(parseDate(post.onlyDate()))
                } else {
                    TODO("Error. Post is empty")
                }
            }
            showView(loadState)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Показывает необходимую View.
     * @param loadState статус загрузки.
     */
    private fun showView(loadState: LoadState?) {
        if (loadState == null) {
            return
        }

        val fade = Fade()
        fade.duration = 300
        TransitionManager.beginDelayedTransition(binding.root, fade)

        binding.newsLoading.loadingFragment.visibility = if (loadState.state == LoadState.State.RUNNING)
            View.VISIBLE else View.GONE
        binding.newsContainer.visibility = if (loadState.state == LoadState.State.SUCCESS)
            View.VISIBLE else View.GONE
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
