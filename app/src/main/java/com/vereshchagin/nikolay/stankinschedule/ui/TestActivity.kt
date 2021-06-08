package com.vereshchagin.nikolay.stankinschedule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R

class TestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test)

        supportActionBar?.title = "Свободные аудитории"
        supportActionBar?.subtitle = "14 апреля 2021"

        val recycler = findViewById<RecyclerView>(R.id.recycler_test)
        recycler.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        val items = assets.open("result.txt").bufferedReader().readLines()

        val adapter = TestAdapter()
        adapter.submitData(items)
        recycler.adapter = adapter
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.activity_menu_test, menu)
//        return true
//    }

    class TestAdapter : RecyclerView.Adapter<TestItem>() {

        private var data = listOf("")

        fun submitData(newData: List<String>) {
            data = newData
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestItem {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
            return TestItem(view)
        }

        override fun onBindViewHolder(holder: TestItem, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

    }

    class TestItem(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val item: TextView = itemView.findViewById(R.id.test_text)

        fun bind(text: String) {
            item.text = text
        }
    }
}