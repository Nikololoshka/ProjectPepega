package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget

import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget.SettingsWidgetAdapter.OnWidgetClickListener
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.WidgetUtils.Companion.scheduleWidgets
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidgetConfigureActivity
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidgetConfigureActivity.Companion.loadPref
import java.util.*

/**
 * Категория настроек виджетов приложения.
 */
class SettingsWidgetFragment : Fragment(), OnWidgetClickListener {

    private var mStatefulLayout: StatefulLayout2 by FragmentDelegate()
    private var mWidgetAdapter: SettingsWidgetAdapter by FragmentDelegate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_widget_settings, container, false)
        val widgetsContainer = view.findViewById<FrameLayout>(R.id.stateful_layout)

        mStatefulLayout = StatefulLayout2.Builder(widgetsContainer)
            .init(StatefulLayout2.LOADING, view.findViewById(R.id.widgets_loading))
            .addView(StatefulLayout2.CONTENT, view.findViewById<View>(R.id.schedule_widgets))
            .addView(StatefulLayout2.EMPTY, view.findViewById<View>(R.id.not_widgets))
            .create()

        val widgetsRecyclerView: RecyclerView = view.findViewById(R.id.recycler_widgets)
        mWidgetAdapter = SettingsWidgetAdapter(this)
        widgetsRecyclerView.adapter = mWidgetAdapter

        return view
    }


    override fun onStart() {
        super.onStart()

        // обновляем список текущих виджетов с расписаниями
        val context = requireContext()
        val ids = scheduleWidgets(context)
        val names: MutableList<String> = ArrayList(ids.size)
        for (id in ids) {
            val data = loadPref(context, id)
            var scheduleName = data.scheduleName
            if (scheduleName != null) {
                if (data.display && data.subgroup !== Subgroup.COMMON) {
                    scheduleName += " " + data.subgroup.toString(context)
                }
                names.add(scheduleName)
            }
        }

        if (names.isEmpty()) {
            mStatefulLayout.setState(StatefulLayout2.EMPTY)
            return
        }

        mWidgetAdapter.submitList(names, ids)
        mStatefulLayout.setState(StatefulLayout2.CONTENT)
    }

    override fun OnWidgetClicked(widgetID: Int) {
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
            context, widgetID, configIntent, PendingIntent.FLAG_UPDATE_CURRENT
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