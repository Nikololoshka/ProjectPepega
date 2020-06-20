package com.vereshchagin.nikolay.stankinschedule.news.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsBinding
import com.vereshchagin.nikolay.stankinschedule.news.NewsAdapter.Companion.DEANERY_NEWS
import com.vereshchagin.nikolay.stankinschedule.news.NewsAdapter.Companion.NEWS_TYPE
import com.vereshchagin.nikolay.stankinschedule.news.NewsAdapter.Companion.UNIVERSITY_NEWS
import com.vereshchagin.nikolay.stankinschedule.news.posts.paging.NewsPostAdapter
import com.vereshchagin.nikolay.stankinschedule.news.repository.network.NetworkState

/**
 * Фрагмент для отображения списка новостей.
 */
class NewsPostFragment  : Fragment(), NewsPostAdapter.OnNewsClickListener {

    private var _binding: ItemNewsBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel фрагмента.
     */
    private lateinit var viewModel: NewsPostViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val type = arguments?.getInt(NEWS_TYPE)
        var newsSubdivision = 125
        type?.let {
            when (it) {
                UNIVERSITY_NEWS -> newsSubdivision = 0
                DEANERY_NEWS -> newsSubdivision = 125
            }
        }

        viewModel = ViewModelProviders.of(this, NewsPostViewModel.Factory(newsSubdivision, context!!))
            .get(NewsPostViewModel::class.java)

        val adapter = NewsPostAdapter(this) {
            viewModel.retry()
        }

        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            adapter.submitList(posts)
        })
        viewModel.networkState.observe(viewLifecycleOwner, Observer { state ->
            adapter.setNetworkState(state)
        })

        binding.newsRecycler.adapter = adapter

        val itemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        binding.newsRecycler.addItemDecoration(itemDecoration)


        viewModel.refreshState.observe(viewLifecycleOwner, Observer {
            binding.newsRefresh.isRefreshing = it == NetworkState.LOADING
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
        Toast.makeText(context, "News: $newsId clicked", Toast.LENGTH_SHORT).show()
    }
}