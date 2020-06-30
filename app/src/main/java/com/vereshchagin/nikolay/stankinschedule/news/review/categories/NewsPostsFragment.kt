package com.vereshchagin.nikolay.stankinschedule.news.review.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsPostsBinding
import com.vereshchagin.nikolay.stankinschedule.news.review.NewsAdapter.Companion.DEANERY_NEWS
import com.vereshchagin.nikolay.stankinschedule.news.review.NewsAdapter.Companion.NEWS_TYPE
import com.vereshchagin.nikolay.stankinschedule.news.review.NewsAdapter.Companion.UNIVERSITY_NEWS
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.paging.NewsPostAdapter
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.network.NetworkState
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.network.Status
import com.vereshchagin.nikolay.stankinschedule.news.viewer.NewsViewerFragment

/**
 * Фрагмент для отображения списка новостей.
 */
class NewsPostsFragment  : Fragment(), NewsPostAdapter.OnNewsClickListener {

    private var _binding: ItemNewsPostsBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel фрагмента.
     */
    private lateinit var viewModel: NewsPostsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemNewsPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // тип новостей
        val type = arguments?.getInt(NEWS_TYPE)
        var newsSubdivision = 125
        type?.let {
            when (it) {
                UNIVERSITY_NEWS -> newsSubdivision = 0
                DEANERY_NEWS -> newsSubdivision = 125
            }
        }

        viewModel = ViewModelProviders.of(this,
            NewsPostsViewModel.Factory(newsSubdivision, activity?.application!!))
            .get(NewsPostsViewModel::class.java)

        val glide = Glide.with(this)
            .setDefaultRequestOptions(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
            )

        val adapter = NewsPostAdapter(this, glide) {
            viewModel.retry()
        }

        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            adapter.submitList(posts)
        })
        viewModel.networkState.observe(viewLifecycleOwner, Observer { state ->
            adapter.setNetworkState(state)
        })

        binding.newsRecycler.adapter = adapter

        // добавление новостей в начало из-за обновления
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                if (viewModel.startRefreshing) {
                    viewModel.newsUpdated()
                    binding.newsRecycler.scrollToPosition(0)
                }
            }
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
            } else {
                binding.newsRefresh.isRefreshing = it == NetworkState.LOADING
            }
        })

        binding.newsRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNewsClick(newsId: Int) {
        val controller = Navigation.findNavController(requireActivity(), R.id.nav_host)
        controller.navigate(R.id.toNewsViewerFragment, NewsViewerFragment.createBundle(newsId))
    }
}