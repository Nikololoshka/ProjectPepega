package com.vereshchagin.nikolay.stankinschedule.news

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentNewsBinding
import com.vereshchagin.nikolay.stankinschedule.news.model.NewsResponse
import com.vereshchagin.nikolay.stankinschedule.news.network.StankinNewsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Фрагмент для новостей университета и деканата.
 */
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel фрагмента.
     */
    private lateinit var viewModel: NewsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // установка адаптора
        binding.newsPager.adapter = NewsAdapter(this)

        // создание tabLayout для pager'а
        TabLayoutMediator(binding.newsTabPager, binding.newsPager, true) {
            tab, position ->
                when (position) {
                    NewsAdapter.UNIVERSITY_NEWS -> tab.setText(R.string.news_university)
                    NewsAdapter.DEANERY_NEWS -> tab.setText(R.string.news_deanery)
                    else -> tab.text = ""
                }
        }.attach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)

        StankinNewsService.instance.getUniversityNews(20, 1).enqueue(object : Callback<NewsResponse> {
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                println(t)
            }

            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                val newsArray = response.body()?.data?.news!!

                for (news in newsArray) {
                    println(news)
                    println("-------")
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}