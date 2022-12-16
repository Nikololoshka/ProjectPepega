package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.pair

import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.LinkContent
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.TextContent
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ViewContent
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.R
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components.LongClickableText

@Composable
fun PairCard(
    pair: ScheduleViewPair,
    pairColors: PairColors,
    onClicked: () -> Unit,
    onLinkClicked: (link: String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimen.ContentPadding),
    enabled: Boolean = true,
    itemSpacing: Dp = 4.dp
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    enabled = enabled,
                    onClick = onClicked
                )
                .padding(contentPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                Text(
                    text = pair.startTime,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pair.endTime,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                Text(
                    text = pair.title,
                    fontSize = 16.sp,
                )

                if (pair.lecturer.isNotEmpty() || !pair.classroom.isEmpty()) {
                    FlowRow(
                        mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                        mainAxisSpacing = itemSpacing,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = pair.lecturer,
                            fontSize = 14.sp,
                        )
                        ClassroomText(
                            classroom = pair.classroom,
                            fontSize = 14.sp,
                            onLinkClicked = onLinkClicked,
                            onClicked = onClicked,
                            interactionSource = interactionSource
                        )
                    }
                }

                FlowRow(
                    mainAxisSpacing = itemSpacing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TypeText(
                        type = pair.type,
                        colors = pairColors
                    )

                    SubgroupText(
                        subgroup = pair.subgroup,
                        colors = pairColors
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassroomText(
    classroom: ViewContent,
    fontSize: TextUnit,
    onClicked: () -> Unit,
    onLinkClicked: (link: String) -> Unit,
    interactionSource: MutableInteractionSource
) {
    when (classroom) {
        is LinkContent -> {
            val context = LocalContext.current
            // TODO("Вынести clipboardManager наверх")
            val clipboardManager = LocalClipboardManager.current

            val linkColor = if (isSystemInDarkTheme()) {
                Color(113, 170, 235)
            } else {
                Color(51, 102, 204)
            }

            LongClickableText(
                text = classroom.toAnnotatedString(fontSize, linkColor),
                onClick = { annotation ->
                    if (annotation?.tag == "URL") {
                        onLinkClicked(annotation.item)
                    } else {
                        onClicked()
                    }
                },
                onLongClick = { annotation ->
                    if (annotation?.tag == "URL") {
                        clipboardManager.setText(AnnotatedString((annotation.item)))
                        Toast.makeText(context, R.string.link_copied, Toast.LENGTH_SHORT).show()
                    }
                },
                interactionSource = interactionSource
            )
        }
        is TextContent -> {
            Text(
                text = classroom.content,
                fontSize = fontSize,
            )
        }
    }
}

private fun LinkContent.toAnnotatedString(
    fontSize: TextUnit,
    linkColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        append(name)

        for (link in links) {
            addStyle(
                style = SpanStyle(
                    color = linkColor,
                    fontSize = fontSize,
                    textDecoration = TextDecoration.Underline
                ),
                start = link.position,
                end = link.position + link.lenght
            )
            addStringAnnotation(
                tag = "URL",
                annotation = link.url,
                start = link.position,
                end = link.position + link.lenght
            )
        }
    }
}

@Composable
private fun TypeText(
    type: Type,
    colors: PairColors
) {
    val typeColor = when (type) {
        Type.LECTURE -> colors.lectureColor
        Type.SEMINAR -> colors.seminarColor
        Type.LABORATORY -> colors.laboratoryColor
    }

    val typeText = when (type) {
        Type.LECTURE -> R.string.type_lecture
        Type.SEMINAR -> R.string.type_seminar
        Type.LABORATORY -> R.string.type_laboratory
    }

    Text(
        text = stringResource(typeText),
        color = textColor(typeColor),
        fontSize = 14.sp,
        modifier = Modifier
            .background(
                color = typeColor,
                shape = RoundedCornerShape(percent = 50)
            )
            .padding(4.dp)
    )
}

@Composable
private fun SubgroupText(
    subgroup: Subgroup,
    colors: PairColors
) {
    val subgroupColor: Color? = when (subgroup) {
        Subgroup.COMMON -> null
        Subgroup.A -> colors.subgroupAColor
        Subgroup.B -> colors.subgroupBColor
    }

    val subgroupText: Int? = when (subgroup) {
        Subgroup.COMMON -> null
        Subgroup.A -> R.string.subgroup_a
        Subgroup.B -> R.string.subgroup_b
    }

    if (subgroupColor != null && subgroupText != null) {
        Text(
            text = stringResource(subgroupText),
            color = textColor(subgroupColor),
            fontSize = 14.sp,
            modifier = Modifier
                .background(
                    color = subgroupColor,
                    shape = RoundedCornerShape(percent = 50)
                )
                .padding(4.dp)
        )
    }
}

@Composable
private fun textColor(
    background: Color,
    isDark: Boolean = background.luminance() < 0.5f
): Color {
    return if (isSystemInDarkTheme()) {
        if (isDark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface
    } else {
        if (isDark) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
    }
}