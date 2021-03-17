package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment

class RepositoryScheduleFragment : BaseFragment<FragmentRepositoryScheduleBinding>() {

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentRepositoryScheduleBinding {
        return FragmentRepositoryScheduleBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {

    }
}