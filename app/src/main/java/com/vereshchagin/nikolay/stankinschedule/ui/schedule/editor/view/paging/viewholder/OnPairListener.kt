package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder

import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type

interface OnPairListener {

    fun onPairClicked(pair: PairItem)

    fun onAddPairClicked(discipline: String, type: Type)
}