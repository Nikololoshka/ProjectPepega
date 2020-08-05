package com.vereshchagin.nikolay.stankinschedule.news.review

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.NewsPostsFragment

/**
 * Адаптер для вкладок новостей: универсетета и деканата.
 */
class NewsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val newsSubdivision: Int = when (position) {
            UNIVERSITY_NEWS -> 0
            DEANERY_NEWS -> 125
            else -> throw RuntimeException("Unknown news type index: $position")
        }
        return NewsPostsFragment(newsSubdivision)
    }

    companion object {
        const val UNIVERSITY_NEWS = 0
        const val DEANERY_NEWS = 1
    }
}