package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostsBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostLoadStateAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.viewer.NewsViewerActivity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

/**
 * Фрагмент для отображения списка новостей.
 */
class NewsPostsFragment : BaseFragment<ItemNewsPostsBinding>() {

    /**
     * ViewModel фрагмента.
     */
    @ExperimentalPagingApi
    private val viewModel by viewModels<NewsPostsViewModel> {
        NewsPostsViewModel.Factory(newsSubdivision, activity?.application!!)
    }

    /**
     * ID отдела, чьи новости необходимо отображать.
     */
    private var newsSubdivision: Int = 0

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ItemNewsPostsBinding {
        return ItemNewsPostsBinding.inflate(inflater, container, false)
    }

    @InternalCoroutinesApi
    @ExperimentalPagingApi
    override fun onPostCreateView(savedInstanceState: Bundle?) {
        // glide для загрузки превью
        val glide = Glide.with(this)
            .setDefaultRequestOptions(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
            )

        newsSubdivision = arguments?.getInt(NEWS_SUBDIVISION)!!

        // адаптер
        val adapter = NewsPostAdapter(this::onNewsClick, glide)
        binding.newsRecycler.adapter = adapter.withLoadStateFooter(
            NewsPostLoadStateAdapter(adapter::retry)
        )

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.newsRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.newsRecycler.scrollToPosition(0) }
        }

        // посты
        viewModel.posts.observe(viewLifecycleOwner, { posts ->
            adapter.submitData(lifecycle, posts)
        })

        // разделитель элементов
        val itemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        binding.newsRecycler.addItemDecoration(itemDecoration)

        binding.newsRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun onNewsClick(newsId: Int) {
        val controller = Navigation.findNavController(requireActivity(), R.id.nav_host)
        controller.navigate(R.id.to_news_viewer_fragment, NewsViewerActivity.createBundle(newsId))
    }

    companion object {

        private const val NEWS_SUBDIVISION = "news_subdivision"

        /**
         * Возвращает объект фрагмента для отображения новостей отдела.
         */
        fun newInstance(newsSubdivision: Int) = NewsPostsFragment().also {
            val args = Bundle()
            args.putInt(NEWS_SUBDIVISION, newsSubdivision)
            it.arguments = args
        }
    }
}