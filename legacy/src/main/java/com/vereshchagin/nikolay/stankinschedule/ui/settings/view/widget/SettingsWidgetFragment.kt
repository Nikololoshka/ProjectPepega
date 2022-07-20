package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget

import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentWidgetSettingsBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget.paging.SettingsWidgetAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget.paging.SettingsWidgetItem
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.WidgetUtils
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.FLAG_MUTABLE_COMPAT
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidgetConfigureActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Категория настроек виджетов приложения.
 */
@AndroidEntryPoint
class SettingsWidgetFragment :
    BaseFragment<FragmentWidgetSettingsBinding>(FragmentWidgetSettingsBinding::inflate) {


    private var statefulLayout: StatefulLayout2 by FragmentDelegate()

    private var adapter: SettingsWidgetAdapter by FragmentDelegate()


    override fun onPostCreateView(savedInstanceState: Bundle?) {
        statefulLayout = StatefulLayout2.Builder(binding.statefulLayout)
            .init(StatefulLayout2.LOADING, binding.widgetsLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.scheduleWidgets)
            .addView(StatefulLayout2.EMPTY, binding.notWidgets)
            .create()

        adapter = SettingsWidgetAdapter(this::onWidgetItemClicked)
        binding.recyclerWidgets.adapter = adapter
    }


    override fun onStart() {
        super.onStart()

        val widgets = updateWidgetList()
        if (widgets.isEmpty()) {
            statefulLayout.setState(StatefulLayout2.EMPTY)
            return
        }

        adapter.submitList(widgets)
        statefulLayout.setState(StatefulLayout2.CONTENT)
    }

    /**
     * Обновляет список текущих виджетов с расписаниями.
     */
    private fun updateWidgetList(): List<SettingsWidgetItem> {
        val widgets = arrayListOf<SettingsWidgetItem>()

        val context = requireContext()
        WidgetUtils.scheduleWidgets(context).forEach { id ->
            val widgetData = ScheduleWidgetConfigureActivity.loadPref(context, id)
            if (widgetData.scheduleName.isNotEmpty()) {
                widgets += SettingsWidgetItem(widgetData.scheduleName, id)
            }
        }

        return widgets
    }

    /**
     * Вызывается когда был нажат элемент из списка с виджетами расписаний.
     * @param widgetID ID виджета расписания.
     */
    private fun onWidgetItemClicked(widgetID: Int) {
        // вызываем конфигурационное окно виджета
        val configIntent = Intent(context, ScheduleWidgetConfigureActivity::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            putExtra(
                ScheduleWidgetConfigureActivity.CONFIGURE_BUTTON_TEXT_EXTRA,
                getString(R.string.widget_schedule_update)
            )
        }

        val configurationPendingIntent = PendingIntent.getActivity(
            context,
            widgetID,
            configIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE_COMPAT
        )

        try {
            configurationPendingIntent.send()

        } catch (ignored: CanceledException) {

        }
    }

    companion object {
        private const val TAG = "SettingsWgtFragmentTag"
    }
}