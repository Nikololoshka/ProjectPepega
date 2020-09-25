package com.vereshchagin.nikolay.stankinschedule.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentAboutBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils

/**
 * Фрагмент вкладки о приложении.
 */
class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentAboutBinding {
        return FragmentAboutBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        binding.appVersion.text = getString(R.string.about_version, BuildConfig.VERSION_NAME)

        binding.aboutPrivacyPolicy.setOnClickListener {
            CommonUtils.openBrowser(requireContext(),
                "https://github.com/Nikololoshka/ProjectPepega/blob/master/Privacy%20Policy.md")
        }
        binding.aboutTerms.setOnClickListener {
            CommonUtils.openBrowser(requireContext(),
                "https://github.com/Nikololoshka/ProjectPepega/blob/master/Terms%20%26%20Conditions.md")
        }
    }
}