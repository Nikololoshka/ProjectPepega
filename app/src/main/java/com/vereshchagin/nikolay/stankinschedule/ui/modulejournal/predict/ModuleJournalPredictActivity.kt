package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityModuleJournalPredictBinding
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict.paging.PredictDisciplineAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2

/**
 * Активность для вычисления рейтинга студента.
 */
class ModuleJournalPredictActivity : AppCompatActivity() {

    private val viewModel by viewModels<ModuleJournalPredictViewModel> {
        ModuleJournalPredictViewModel.Factory(application)
    }

    private lateinit var statefulLayout: StatefulLayout2
    private lateinit var binding: ActivityModuleJournalPredictBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityModuleJournalPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setOnClickListener {
            changeSemester()
        }

        viewModel.semester.observe(this) {
            binding.toolbar.subtitle = it
        }

        val adapter = PredictDisciplineAdapter { item, value ->
            viewModel.updateMark(item.title, item.type, value)
        }

        binding.recyclerView.adapter = adapter

        viewModel.semesterMarks.observe(this) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }

        viewModel.showAllDiscipline.observe(this) {
            adapter.showDisciplines(it)
        }

        viewModel.rating.observe(this) {
            binding.rating.text = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_module_journal_predict, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.show_all_disciplines)?.isChecked =
            viewModel.showAllDiscipline.value == true

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // смена семестра для расчета рейтинга
            R.id.change_semester -> {
                changeSemester()
                return true
            }
            // показывать все дисциплины для расчета рейтинга
            R.id.show_all_disciplines -> {
                viewModel.updateShowDisciplines(!item.isChecked)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeSemester() {
        val semesters = viewModel.semestersArray.value
        if (semesters.isNullOrEmpty()) {
            return
        }

        var selectedSemester = viewModel.semester.value ?: return
        val currentSemester = semesters.indexOf(selectedSemester)

        MaterialAlertDialogBuilder(this)
            .setTitle("Выберите семестр")
            .setSingleChoiceItems(semesters, currentSemester) { _, which ->
                selectedSemester = semesters[which]
            }
            .setNeutralButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                viewModel.updateSemesterMarks(selectedSemester)
                dialog.cancel()
            }
            .show()
    }

    companion object {
        private const val TAG = "MJPredictActivityLog"
    }
}