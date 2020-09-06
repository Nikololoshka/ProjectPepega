package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.api.NetworkState
import com.vereshchagin.nikolay.stankinschedule.api.Status
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostsBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.NewsPostAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.viewer.NewsViewerFragment

/**
 * Фрагмент для отображения списка новостей.
 */
class NewsPostsFragment: BaseFragment<ItemNewsPostsBinding>(), NewsPostAdapter.OnNewsClickListener {

    /**
     * ViewModel фрагмента.
     */
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
        val adapter = NewsPostAdapter(this, glide) {
            viewModel.retry()
        }
        binding.newsRecycler.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0 && viewModel.isScrollToTop()) {
                    binding.newsRecycler.scrollToPosition(0)
                }
            }
        })

        // посты
        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            adapter.submitList(posts)
            if (viewModel.isStartRefreshing()) {
                binding.newsRecycler.scrollToPosition(0)
                viewModel.newsUpdated()
            }
        })

        // сеть
        viewModel.networkState.observe(viewLifecycleOwner, Observer { state ->
            adapter.setNetworkState(state)
        })

        // разделитель элементов
        val itemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        binding.newsRecycler.addItemDecoration(itemDecoration)

        // состояние обновления
        viewModel.refreshState.observe(viewLifecycleOwner, Observer {
            if (it.status == Status.FAILED) {
                val message = it.msg ?: "Unknown error"
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                    .show()
            }
            binding.newsRefresh.isRefreshing = it == NetworkState.LOADING
        })

        binding.newsRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun onNewsClick(newsId: Int) {
        val controller = Navigation.findNavController(requireActivity(), R.id.nav_host)
        controller.navigate(R.id.toNewsViewerFragment, NewsViewerFragment.createBundle(newsId))
    }

    companion object {

        private const val NEWS_SUBDIVISION = "news_subdivision"

        fun newInstance(newsSubdivision: Int) = NewsPostsFragment().also {
            val args = Bundle()
            args.putInt(NEWS_SUBDIVISION, newsSubdivision)
            it.arguments = args
        }
    }
}