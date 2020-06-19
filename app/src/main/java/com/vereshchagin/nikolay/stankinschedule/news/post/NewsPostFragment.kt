package com.vereshchagin.nikolay.stankinschedule.news.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsBinding
import com.vereshchagin.nikolay.stankinschedule.news.post.paging.NewsPostAdapter

/**
 * Фрагмент для отображения списка новостей.
 */
class NewsPostFragment  : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // тип новостей
        arguments?.let {
            println(it.getInt("type"))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, NewsPostViewModel.Factory(context!!))
            .get(NewsPostViewModel::class.java)

        val adapter = NewsPostAdapter {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}