package com.vereshchagin.nikolay.stankinschedule.news.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.NewsPostsFragment

/**
 * Адаптер для вкладок новостей: универсетета и деканата.
 */
class NewsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = NewsPostsFragment().apply {
        val bundle = Bundle()
        when (position) {
            UNIVERSITY_NEWS -> bundle.putInt(NEWS_TYPE, UNIVERSITY_NEWS)
            DEANERY_NEWS -> bundle.putInt(NEWS_TYPE, DEANERY_NEWS)
        }
        arguments = bundle
    }

    companion object {
        const val NEWS_TYPE = "type"

        const val UNIVERSITY_NEWS = 0;
        const val DEANERY_NEWS = 1;
    }
}