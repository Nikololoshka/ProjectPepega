package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityRepositoryBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseActivity

/**
 *
 */
class RepositoryActivity : BaseActivity<ActivityRepositoryBinding>() {

    override fun onInflateView(): ActivityRepositoryBinding {
        return ActivityRepositoryBinding.inflate(layoutInflater)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}