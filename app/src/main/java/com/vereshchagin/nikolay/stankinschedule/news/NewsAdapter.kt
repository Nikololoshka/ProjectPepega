package com.vereshchagin.nikolay.stankinschedule.news

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vereshchagin.nikolay.stankinschedule.news.posts.NewsPostFragment

/**
 * Адаптер для вкладок новостей: универсетета и деканата.
 */
class NewsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = NewsPostFragment().apply {
        arguments = when (position) {
            UNIVERSITY_NEWS -> bundleOf(NEWS_TYPE to UNIVERSITY_NEWS)
            DEANERY_NEWS -> bundleOf(NEWS_TYPE to DEANERY_NEWS)
            else -> null
        }
    }

    companion object {
        const val NEWS_TYPE = "type"

        const val UNIVERSITY_NEWS = 0;
        const val DEANERY_NEWS = 1;
    }
}