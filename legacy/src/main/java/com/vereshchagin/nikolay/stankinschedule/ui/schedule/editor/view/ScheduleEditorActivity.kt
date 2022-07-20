package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityScheduleEditorBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type
import com.vereshchagin.nikolay.stankinschedule.ui.BaseActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.PairEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.ScheduleEditorAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder.OnPairListener
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Фрагмент с списком пар для редактирования.
 */
@AndroidEntryPoint
class ScheduleEditorActivity :
    BaseActivity<ActivityScheduleEditorBinding>(ActivityScheduleEditorBinding::inflate),
    OnPairListener {

    @Inject
    lateinit var viewModelFactory: ScheduleEditorViewModel.ScheduleEditorFactory

    /**
     * ViewModel фрагмента.
     */
    private val viewModel: ScheduleEditorViewModel by viewModels {
        ScheduleEditorViewModel.provideFactory(viewModelFactory, scheduleId)
    }

    private lateinit var statefulLayout: StatefulLayout2

    private var scheduleId: Long = -1

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        statefulLayout = StatefulLayout2.Builder(binding.disciplinesContainer)
            .init(StatefulLayout2.LOADING, binding.disciplinesLoading.root)
            .addView(StatefulLayout2.EMPTY, binding.disciplinesEmpty)
            .addView(StatefulLayout2.CONTENT, binding.disciplines)
            .create()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // получение данных
        scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1)


        val adapter = ScheduleEditorAdapter(this)
        binding.disciplines.adapter = adapter
        binding.disciplines.addItemDecoration(
            DividerItemDecoration(this, LinearLayout.VERTICAL)
        )

        binding.addPair.setOnClickListener {
            onAddPairClicked(null, null)
        }

        // скрытие кнопки при прокрутке
        binding.disciplines.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.addPair.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((dy > 0 || dy < 0) && binding.addPair.isShown) {
                    binding.addPair.hide()
                }
            }
        })

        viewModel.disciplines.observe(this) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }

        viewModel.state.observe(this, this::onScheduleStateChanged)
    }

    override fun onPairClicked(pair: PairItem) {
        val intent = PairEditorActivity.editPairIntent(this, scheduleId, pair.id)
        startActivity(intent)
    }

    override fun onAddPairClicked(discipline: String?, type: Type?) {
        val intent = PairEditorActivity.newPairIntent(this, scheduleId, discipline, type)
        startActivity(intent)
    }

    private fun onScheduleStateChanged(state: ScheduleEditorViewModel.ScheduleState) {
        when (state) {
            ScheduleEditorViewModel.ScheduleState.SUCCESSFULLY_LOADED -> {
                statefulLayout.setState(StatefulLayout2.CONTENT)
            }
            ScheduleEditorViewModel.ScheduleState.SUCCESSFULLY_LOADED_EMPTY -> {
                statefulLayout.setState(StatefulLayout2.EMPTY)
            }
            ScheduleEditorViewModel.ScheduleState.LOADING -> {
                statefulLayout.setState(StatefulLayout2.LOADING)
            }
        }
    }

    companion object {

        private const val TAG = "ScheduleEditorLog"
        private const val EXTRA_SCHEDULE_ID = "extra_schedule_id"

        /**
         *  Создает intent для вызова редактора расписания.
         */
        fun createIntent(context: Context, scheduleId: Long): Intent {
            return Intent(context, ScheduleEditorActivity::class.java).apply {
                putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            }
        }
    }
}