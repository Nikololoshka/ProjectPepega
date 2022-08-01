package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.predict

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.core.utils.setVisibility
import com.vereshchagin.nikolay.stankinschedule.modulejournal.databinding.ActivityPredictBinding
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.predict.components.PredictRatingPanel
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.predict.paging.PredictAdapter
import com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.predict.view.SemesterSelectorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PredictActivity : AppCompatActivity() {

    private val viewModel: PredictViewModel by viewModels()

    private lateinit var binding: ActivityPredictBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.toolbar.setOnClickListener { showSemesterSelector() }
        // setSupportActionBar(binding.toolbar)

        setupPanel(predictedRating = 0f, showExposedMarks = false)

        supportFragmentManager.setFragmentResultListener(
            SemesterSelectorDialog.REQUEST_SEMESTER_SELECTOR, this, ::onSemesterSelectorResult
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentSemester.collect {
                    binding.toolbar.subtitle = it
                }
            }
        }

        val adapter = PredictAdapter(
            onMarkChange = { mark, value ->
                viewModel.updatePredictMark(mark, value)
            },
            onItemCountChanged = { count ->
                if (count > 0) {
                    showUI(showContent = true)
                } else {
                    showUI(showEmpty = true)
                }
            }
        )
        binding.predictMarks.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.predictMarks.collect { data ->
                    adapter.submitData(data)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.panelState.collect { (predictedRating, showExposedMarks) ->
                    setupPanel(predictedRating, showExposedMarks)
                    adapter.showExposedItems(showExposedMarks)
                }
            }
        }
    }

    private fun showUI(
        showContent: Boolean = false,
        showLoading: Boolean = false,
        showEmpty: Boolean = false,
    ) {
        binding.predictMarks.setVisibility(showContent)
        binding.predictLoading.setVisibility(showLoading)
        binding.predictNoDisciplines.setVisibility(showEmpty)
    }

    private fun setupPanel(predictedRating: Float, showExposedMarks: Boolean) {
        binding.ratingPanel.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    PredictRatingPanel(
                        predictedRating = predictedRating,
                        showExposedMarks = showExposedMarks,
                        onChangeSemester = { showSemesterSelector() },
                        onShowExposedMarks = { viewModel.toggleShowExposedMarks() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    private fun onSemesterSelectorResult(requestKey: String, result: Bundle) {
        if (requestKey == SemesterSelectorDialog.REQUEST_SEMESTER_SELECTOR) {
            val newSemester = result.getString(SemesterSelectorDialog.RESULT_SEMESTER) ?: return
            viewModel.changeSemester(newSemester)
        }
    }


    private fun showSemesterSelector() {
        val currentSemester = viewModel.currentSemester.value
        val semesters = viewModel.semesters.value

        if (currentSemester.isEmpty() || semesters.isEmpty()) {
            return
        }

        val dialog = SemesterSelectorDialog.newInstance(semesters, currentSemester)
        dialog.show(supportFragmentManager, SemesterSelectorDialog.REQUEST_SEMESTER_SELECTOR)
    }
}