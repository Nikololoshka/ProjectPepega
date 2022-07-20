package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityModuleJournalPredictBinding
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict.paging.PredictDisciplineAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.ActivityDelegate
import dagger.hilt.android.AndroidEntryPoint

/**
 * Активность для вычисления рейтинга студента.
 */
@AndroidEntryPoint
class ModuleJournalPredictActivity : AppCompatActivity() {

    private val viewModel: ModuleJournalPredictViewModel by viewModels()

    private var statefulLayout: StatefulLayout2 by ActivityDelegate()
    private lateinit var binding: ActivityModuleJournalPredictBinding

    /**
     * Аниматор для изменения рейтинга.
     */
    private val ratingAnimator = ValueAnimator().apply {
        duration = 300
        interpolator = DecelerateInterpolator()
        addUpdateListener(this@ModuleJournalPredictActivity::ratingAnimatorUpdate)
    }

    /**
     * Текущий, отображаемый рейтинг
     */
    private var currentRating = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityModuleJournalPredictBinding.inflate(layoutInflater)
        statefulLayout = StatefulLayout2.Builder(binding.predictContainer)
            .init(StatefulLayout2.LOADING, binding.predictLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.predictRecyclerView)
            .addView(StatefulLayout2.EMPTY, binding.predictNoDisciplines)
            .create()

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // настройка toolbar'а
        binding.toolbar.setOnClickListener {
            changeSemester()
        }
        viewModel.semester.observe(this) {
            binding.toolbar.subtitle = it
        }

        // список с дисциплинами
        val adapter = PredictDisciplineAdapter({ item, value ->
            viewModel.updateMark(item.title, item.type, value)
        }, { count ->
            if (count == 0) {
                statefulLayout.setState(StatefulLayout2.EMPTY)
            } else {
                statefulLayout.setState(StatefulLayout2.CONTENT)
            }
        })
        binding.predictRecyclerView.adapter = adapter
        binding.predictRecyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayout.VERTICAL)
        )

        viewModel.semesterMarks.observe(this) {
            if (it == null) {
                statefulLayout.setState(StatefulLayout2.LOADING)
            } else {
                adapter.submitData(lifecycle, it)
            }
        }

        viewModel.showAllDiscipline.observe(this) {
            adapter.showDisciplines(it)
        }

        viewModel.rating.observe(this) {
            startUpdateRating(it.toFloat())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_module_journal_predict, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
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

    /**
     * Вызывается при анимации изменения рейтинга для
     * установки промежуточного значения.
     */
    private fun ratingAnimatorUpdate(animator: ValueAnimator) {
        val rating = animator.animatedValue as Float
        updateRating(rating)
    }

    /**
     * Устанавливает новое значение рейтинга в UI
     */
    @SuppressLint("SetTextI18n")
    private fun updateRating(rating: Float) {
        binding.rating.text = "%.2f".format(rating)
    }

    /**
     * Начинает обновления текущего рейтинга в UI.
     */
    private fun startUpdateRating(rating: Float) {
        when {
            // отсутствует рейтинг
            rating == 0.0F -> {
                binding.rating.text = "--.--"
            }
            // первый раз (установить сразу, без анимации)
            currentRating == 0.0F -> {
                currentRating = rating
                updateRating(rating)
            }
            // изменение с анимацией
            else -> {
                val oldRating = currentRating
                currentRating = rating
                ratingAnimator.apply {
                    setFloatValues(oldRating, rating)
                    start()
                }
            }
        }
    }

    private fun changeSemester() {
        val semesters = viewModel.semestersArray.value
        if (semesters.isNullOrEmpty()) {
            return
        }

        var selectedSemester = viewModel.semester.value ?: return
        val currentSemester = semesters.indexOf(selectedSemester)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.mj_select_semester)
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