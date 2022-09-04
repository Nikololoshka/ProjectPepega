package com.vereshchagin.nikolay.stankinschedule.journal.predict.ui.paging

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.recyclerview.widget.RecyclerView

abstract class ComposeRecyclerHolder(
    val composeView: ComposeView,
) : RecyclerView.ViewHolder(composeView) {

    init {
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
    }
}