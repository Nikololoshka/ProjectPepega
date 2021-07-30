package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostsListBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostLoadStateAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.viewer.NewsViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

/**
 * Фрагмент для отображения списка новостей.
 */
@ExperimentalPagingApi
@AndroidEntryPoint
class NewsPostsFragment :
    BaseFragment<ItemNewsPostsListBinding>(ItemNewsPostsListBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: NewsPostsViewModel.NewsPostsFactory

    /**
     * ViewModel фрагмента.
     */
    private val viewModel: NewsPostsViewModel by viewModels {
        NewsPostsViewModel.provideFactory(viewModelFactory, newsSubdivision)
    }

    /**
     * ID отдела, чьи новости необходимо отображать.
     */
    private var newsSubdivision: Int = 0

    @InternalCoroutinesApi
    override fun onPostCreateView(savedInstanceState: Bundle?) {
        // glide для загрузки пред просмотра
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

        // посты
        viewModel.posts.observe(viewLifecycleOwner, { posts ->
            adapter.submitData(lifecycle, posts)
        })

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading && it.mediator != null }
                .collect {
                    binding.newsRecycler.scrollToPosition(0)
                }
        }

        // разделитель элементов
        val itemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        binding.newsRecycler.addItemDecoration(itemDecoration)

        binding.newsRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    /**
     * Вызывается при нажатии на новость в списке.
     */
    private fun onNewsClick(newsId: Int, newsTitle: String?) {
        val intent = NewsViewerActivity.newsIntent(requireContext(), newsId, newsTitle)
        startActivity(intent)
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