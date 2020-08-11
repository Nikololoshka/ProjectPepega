package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityPairEditorBinding
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.Pair

class PairEditorActivity2 : AppCompatActivity() {

    companion object {

        private const val EXTRA_SCHEDULE_NAME = "extra_schedule"
        private const val EXTRA_PAIR = "extra_pair"

        fun newIntent(context: Context, scheduleName: String, pair: Pair): Intent {
            val intent = Intent(context, PairEditorActivity2::class.java)
            intent.putExtra(EXTRA_SCHEDULE_NAME, scheduleName)
            intent.putExtra(EXTRA_PAIR, pair)
            return intent
        }
    }

    private lateinit var binding: ActivityPairEditorBinding
    private var editablePair: Pair? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPairEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFields()

        if (savedInstanceState != null) {

        } else {
            editablePair = intent.getParcelableExtra(EXTRA_PAIR)
        }
    }

    private fun initFields() {
//        binding.editTextTitle.setAdapter(
//            ArrayAdapter(this, R.layout.dropdown_item_multiline,
//            R.id.dropdown_item, readVariants(R.raw.titles))
//        )
//        binding.editTextLecturer.setAdapter(
//            ArrayAdapter(this, R.layout.dropdown_item_multiline,
//                R.id.dropdown_item, readVariants(R.raw.lecturers))
//        )
//        binding.editTextClassroom.setAdapter(
//            ArrayAdapter(this, R.layout.dropdown_item_multiline,
//                R.id.dropdown_item, readVariants(R.raw.classrooms))
//        )

        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            R.id.dropdown_item, resources.getTextArray(R.array.type_list))
        binding.spinnerType2.setAdapter(typeAdapter)
        binding.spinnerType2.setText(typeAdapter.getItem(0))

        val subgroupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            R.id.dropdown_item, resources.getTextArray(R.array.subgroup_list))
        binding.spinnerSubgroup2.setAdapter(subgroupAdapter)
        binding.spinnerSubgroup2.setText(subgroupAdapter.getItem(0))

        val timeStartAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            R.id.dropdown_item, resources.getTextArray(R.array.time_start_list))
        binding.spinnerTimeStart2.setAdapter(timeStartAdapter)
        binding.spinnerTimeStart2.setText(timeStartAdapter.getItem(0))

        val timeEndAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            R.id.dropdown_item, resources.getTextArray(R.array.time_end_list))
        binding.spinnerTimeEnd2.setAdapter(timeEndAdapter)
        binding.spinnerTimeEnd2.setText(timeEndAdapter.getItem(0))



        binding.spinnerTimeStart2.setOnItemClickListener { parent, view, position, id ->
            var newPos = 0

            binding.spinnerTimeEnd2.text

            if (newPos > 7 - position) {
                newPos = 0
            }
        }
    }

    private fun readVariants(@RawRes id: Int): List<String> {
        return resources.openRawResource(id).bufferedReader().lineSequence().toList()
    }
}