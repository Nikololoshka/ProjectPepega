package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityDateEditorBinding

class DateEditorActivity2 : AppCompatActivity() {
    companion object {

    }

    private lateinit var binding: ActivityDateEditorBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityDateEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}