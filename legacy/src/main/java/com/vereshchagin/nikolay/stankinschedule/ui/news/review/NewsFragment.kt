package com.vereshchagin.nikolay.stankinschedule.ui.news.review

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentNewsBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment

/**
 * Фрагмент для новостей университета и деканата.
 */
class NewsFragment : BaseFragment<FragmentNewsBinding>(FragmentNewsBinding::inflate) {

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        // установка адаптера
        binding.newsPager.adapter = NewsAdapter(this)
        binding.newsPager.offscreenPageLimit = 2

        // создание tabLayout для pager'а
        TabLayoutMediator(binding.newsTabPager, binding.newsPager, true) { tab, position ->
            when (position) {
                NewsAdapter.UNIVERSITY_NEWS -> tab.setText(R.string.news_university)
                NewsAdapter.DEANERY_NEWS -> tab.setText(R.string.news_deanery)
                else -> throw IndexOutOfBoundsException("Unknown news index: $position")
            }
        }.attach()

        trackScreen(TAG, MainActivity.TAG)
    }

    companion object {
        private const val TAG = "NewsFragment"
    }
}