package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.AppBarLayout
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentModuleJournalBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment

/**
 * Фрагмент модульного журнала с оценками.
 */
class ModuleJournalFragmentKt : BaseFragment<FragmentModuleJournalBinding>() {

    private var viewModel = viewModels<ModuleJournalViewModel>()

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentModuleJournalBinding {
        return FragmentModuleJournalBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {

        binding.appBarMj.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.mjContent.isEnabled = verticalOffset == 0
        })
        binding.mjContent.setOnRefreshListener(this::refreshAll)
    }

    private fun refreshAll() {

    }
}