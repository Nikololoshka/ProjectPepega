package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder

import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type

interface OnPairListener {

    fun onPairClicked(pair: Pair)

    fun onAddPairClicked(discipline: String, type: Type)
}