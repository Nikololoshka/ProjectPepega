package com.vereshchagin.nikolay.stankinschedule.news.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNewsBinding

/**
 * Фрагмент для отображения списка новостей.
 */
class NewsPostFragment  : Fragment() {

    private var _binding: ItemNewsBinding? = null
    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}