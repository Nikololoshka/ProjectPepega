package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.components

import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SemesterTabRow(
    semesters: List<String>,
    currentPage: Int,
    onPageScrolled: suspend (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerScroller = rememberCoroutineScope()

    ScrollableTabRow(
        selectedTabIndex = currentPage,
        edgePadding = 0.dp,
        modifier = modifier
    ) {
        semesters.forEachIndexed { index, semester ->
            Tab(
                selected = index == currentPage,
                onClick = { pagerScroller.launch { onPageScrolled(index) } },
                text = { Text(text = semester) }
            )
        }
    }
}