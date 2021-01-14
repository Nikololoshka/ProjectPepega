package com.vereshchagin.nikolay.stankinschedule.view

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class DropDownAdapter<T>(
    context: Context, objects: List<T>
) : ArrayAdapter<T>(context, android.R.layout.simple_list_item_1, objects) {

    constructor(context: Context, objects: Array<T>) : this(context, objects.asList())

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

            }
        }
    }
}