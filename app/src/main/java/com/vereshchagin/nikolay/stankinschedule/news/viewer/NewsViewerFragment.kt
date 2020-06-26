package com.vereshchagin.nikolay.stankinschedule.news.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentNewsViewerBinding

/**
 * Фрагмент для просмотра новости.
 */
class NewsViewerFragment : Fragment() {

    private var _binding: FragmentNewsViewerBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel фрагмента.
     */
    private lateinit var viewModel: NewsViewerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewsViewerViewModel::class.java)

        val newsId = arguments?.getInt(NEWS_ID)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val NEWS_ID = "news_id"

        fun createBundle(newsId: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt(NEWS_ID, newsId)
            return bundle
        }
    }
}
