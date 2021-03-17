package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositorySearchBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment

class RepositorySearchFragment : BaseFragment<FragmentRepositorySearchBinding>() {

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentRepositorySearchBinding {
        return FragmentRepositorySearchBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {

    }
}