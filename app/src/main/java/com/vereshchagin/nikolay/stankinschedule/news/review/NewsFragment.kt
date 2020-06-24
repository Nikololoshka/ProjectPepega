package com.vereshchagin.nikolay.stankinschedule.news.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentNewsBinding

/**
 * Фрагмент для новостей университета и деканата.
 */
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

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
        binding.newsPager.offscreenPageLimit = 2

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}