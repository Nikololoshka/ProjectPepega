package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityScheduleEditorBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type
import com.vereshchagin.nikolay.stankinschedule.ui.BaseActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.PairEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.ScheduleEditorAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder.OnPairListener

/**
 * Фрагмент с списком пар для редактирования.
 */
class ScheduleEditorActivity : BaseActivity<ActivityScheduleEditorBinding>(), OnPairListener {

    /**
     * ViewModel фрагмента.
     */
    private lateinit var viewModel: ScheduleEditorViewModel
    private lateinit var scheduleName: String

    override fun onInflateView(): ActivityScheduleEditorBinding {
        return ActivityScheduleEditorBinding.inflate(layoutInflater)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scheduleName = intent.getStringExtra(EXTRA_SCHEDULE_NAME)!!

        viewModel = ViewModelProvider(
            this,
            ScheduleEditorViewModel.Factory(application, scheduleName)
        ).get(ScheduleEditorViewModel::class.java)


        val adapter = ScheduleEditorAdapter(this)
        binding.disciplineRecycler.adapter = adapter
        binding.disciplineRecycler.addItemDecoration(
            DividerItemDecoration(this, LinearLayout.VERTICAL)
        )

        viewModel.disciplines.observe(this) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }
    }

    override fun onPairClicked(pair: PairItem) {
        val intent = PairEditorActivity.editPairIntent(this, scheduleName, pair.id)
        startActivity(intent)
    }

    override fun onAddPairClicked(discipline: String, type: Type) {
        val intent = PairEditorActivity.newPairIntent(this, scheduleName, discipline, type)
        startActivity(intent)
    }

    companion object {

        private const val TAG = "ScheduleEditorLog"
        private const val EXTRA_SCHEDULE_NAME = "schedule_name"

        /**
         *  Создает intent для вызова редактора расписания.
         */
        fun createIntent(context: Context, scheduleName: String): Intent {
            return Intent(context, ScheduleEditorActivity::class.java).apply {
                putExtra(EXTRA_SCHEDULE_NAME, scheduleName)
            }
        }
    }
}