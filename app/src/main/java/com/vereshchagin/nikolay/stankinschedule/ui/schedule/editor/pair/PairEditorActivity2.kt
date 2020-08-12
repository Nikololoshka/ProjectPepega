package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityPairEditorBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*
import com.vereshchagin.nikolay.stankinschedule.utils.DropDownAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.currentPosition
import com.vereshchagin.nikolay.stankinschedule.utils.setCurrentPosition

/**
 * Активность редактирования пары.
 */
class PairEditorActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityPairEditorBinding

    private lateinit var request: Request
    private lateinit var date: Date
    private var editablePair: Pair? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPairEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFields()

        request = intent.getSerializableExtra(EXTRA_REQUEST) as Request
        editablePair = intent.getParcelableExtra(EXTRA_PAIR)

        if (savedInstanceState != null) {
            date = savedInstanceState.getParcelable(DATE_PAIR)!!

        } else {
            if (request == Request.EDIT_PAIR) {
                editablePair!!.let {
                    date = it.date
                    bind(it)
                }
            } else {
                date = Date()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(DATE_PAIR, date)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreBind()
    }

    private fun initFields() {
        initAutoComplete(binding.spinnerType2, resourcesArray(R.array.type_list))
        initAutoComplete(binding.spinnerSubgroup2, resourcesArray(R.array.subgroup_list))
        initAutoComplete(binding.spinnerTimeStart2, Time.STARTS)
        initAutoComplete(binding.spinnerTimeEnd2, Time.ENDS)

        // Listener для ограничения списка окончания пар, при смене начала пары.
        binding.spinnerTimeStart2.setOnItemClickListener { _, _, position, _ ->
            var newPos = binding.spinnerTimeEnd2.currentPosition()

            val times = Time.ENDS.subList(position, Time.ENDS.size)
            val adapter = DropDownAdapter(baseContext, times)
            binding.spinnerTimeEnd2.setAdapter(adapter)

            if (newPos > 7 - position) {
                newPos = 0
            }

            binding.spinnerTimeEnd2.setText(times[newPos], false)
        }
    }

    private fun initAutoComplete(autoComplete: MaterialAutoCompleteTextView, objects: List<String>) {
        val adapter = DropDownAdapter(this, objects)
        autoComplete.setAdapter(adapter)

        if (autoComplete.text.isNullOrEmpty()) {
            autoComplete.setText(adapter.getItem(0), false)
        }
    }

    private fun bind(pair: Pair) {
        binding.editTextTitle.setText(pair.title)
        binding.editTextLecturer.setText(pair.lecturer)
        binding.editTextClassroom.setText(pair.classroom)
        setCurrentType(pair.type)
        setCurrentSubgroup(pair.subgroup)
        setCurrentTime(pair.time)
    }

    private fun restoreBind() {
        val position = Time.ENDS.indexOf(binding.spinnerTimeEnd2.text.toString())
        Log.d("MyLog", "restoreBind: $position")
        Log.d("MyLog", "restoreBind: ${binding.spinnerTimeEnd2.text}")

        val times = Time.ENDS.subList(position, Time.ENDS.size)
        val adapter = DropDownAdapter(baseContext, times)
        binding.spinnerTimeEnd2.setAdapter(adapter)
    }

    private fun resourcesArray(id: Int): List<String> {
        return resources.getStringArray(id).asList()
    }

    private fun setCurrentType(type: Type) {
        val pos = listOf(
            Type.LECTURE, Type.SEMINAR, Type.LABORATORY
        ).indexOf(type)

        binding.spinnerType2.setCurrentPosition(pos)
    }

    private fun currentType(): Type {
        return listOf(
            Type.LECTURE, Type.SEMINAR, Type.LABORATORY
        )[binding.spinnerType2.currentPosition()]
    }

    private fun setCurrentSubgroup(subgroup: Subgroup) {
        val pos = listOf(
            Subgroup.COMMON, Subgroup.A, Subgroup.B
        ).indexOf(subgroup)

        binding.spinnerSubgroup2.setCurrentPosition(pos)
    }

    private fun setCurrentTime(time: Time) {
        binding.spinnerTimeStart2.setCurrentPosition(time.number())
        binding.spinnerTimeEnd2.setCurrentPosition(time.duration - 1)
    }

    private fun currentSubgroup(): Subgroup {
        return listOf(
            Subgroup.COMMON, Subgroup.A, Subgroup.B
        )[binding.spinnerSubgroup2.currentPosition()]
    }

    private fun currentStartTime(): String {
        return binding.spinnerTimeStart2.text.toString()
    }

    private fun currentEndTime(): String {
        return binding.spinnerTimeEnd2.text.toString()
    }

    enum class Request {
        NEW_PAIR,
        EDIT_PAIR
    }

    companion object {

        private const val EXTRA_SCHEDULE_NAME = "extra_schedule"
        private const val EXTRA_PAIR = "extra_pair"
        private const val EXTRA_REQUEST = "extra_request"

        private const val DATE_PAIR = "date_pair"

        fun newPairIntent(context: Context, scheduleName: String): Intent {
            val intent = Intent(context, PairEditorActivity2::class.java)
            intent.putExtra(EXTRA_SCHEDULE_NAME, scheduleName)
            intent.putExtra(EXTRA_REQUEST, Request.NEW_PAIR)
            return intent
        }

        fun newEditIntent(context: Context, scheduleName: String, pair: Pair) : Intent {
            val intent = Intent(context, PairEditorActivity2::class.java)
            intent.putExtra(EXTRA_SCHEDULE_NAME, scheduleName)
            intent.putExtra(EXTRA_PAIR, pair)
            intent.putExtra(EXTRA_REQUEST, Request.EDIT_PAIR)
            return intent
        }
    }
}