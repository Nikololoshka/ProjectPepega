package com.vereshchagin.nikolay.stankinschedule.ui.about

import android.os.Bundle
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentAboutBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils
import com.vereshchagin.nikolay.stankinschedule.utils.Constants

/**
 * Фрагмент вкладки о приложении.
 */
class AboutFragment : BaseFragment<FragmentAboutBinding>(FragmentAboutBinding::inflate) {

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        binding.appVersion.text = getString(R.string.about_version, BuildConfig.VERSION_NAME)

        binding.aboutPrivacyPolicy.setOnClickListener {
            CommonUtils.openBrowser(requireContext(), Constants.PRIVACY_POLICY_URL)
        }

        binding.aboutTerms.setOnClickListener {
            CommonUtils.openBrowser(requireContext(), Constants.TERMS_CONDITIONS_URL)
        }
    }
}