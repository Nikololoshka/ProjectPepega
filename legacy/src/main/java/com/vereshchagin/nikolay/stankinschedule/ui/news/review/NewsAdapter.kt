package com.vereshchagin.nikolay.stankinschedule.ui.news.review

import androidx.fragment.app.Fragment
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.NewsPostsFragment

/**
 * Адаптер для вкладок новостей: университета и деканата.
 */
class NewsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    @OptIn(ExperimentalPagingApi::class)
    override fun createFragment(position: Int): Fragment {
        val newsSubdivision: Int = when (position) {
            UNIVERSITY_NEWS -> 0
            DEANERY_NEWS -> 125
            else -> throw RuntimeException("Unknown news type index: $position")
        }
        return NewsPostsFragment.newInstance(newsSubdivision)
    }

    companion object {
        const val UNIVERSITY_NEWS = 0
        const val DEANERY_NEWS = 1
    }
}