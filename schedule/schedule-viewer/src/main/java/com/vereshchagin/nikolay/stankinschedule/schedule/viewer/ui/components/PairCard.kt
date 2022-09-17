package com.vereshchagin.nikolay.stankinschedule.schedule.viewer.ui.components

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.vereshchagin.nikolay.stankinschedule.core.ui.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.R
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ScheduleViewPair
import com.vereshchagin.nikolay.stankinschedule.schedule.viewer.domain.model.ViewContent

@Composable
fun PairCard(
    pair: ScheduleViewPair,
    pairColors: PairColors,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimen.ContentPadding),
    itemSpacing: Dp = 4.dp
) {
    Card(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = true,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClassroomText(
    classroom: ViewContent,
    fontSize: TextUnit,
) {
    when (classroom) {
        is ViewContent.LinkContent -> {
            val context = LocalContext.current
            val clipboardManager = LocalClipboardManager.current

            val linkColor = if (isSystemInDarkTheme()) {
                Color(113, 170, 235)
            } else {
                Color(51, 102, 204)
            }

            Text(
                text = classroom.name,
                style = TextStyle(
                    fontSize = fontSize,
                    textDecoration = TextDecoration.Underline,
                    color = linkColor
                ),
                modifier = Modifier
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            BrowserUtils.openLink(
                                context = context,
                                url = classroom.link,
                                includeApp = true
                            )
                        },
                        onLongClick = {
                            clipboardManager.setText(AnnotatedString((classroom.link)))
                            Toast.makeText(context, R.string.link_copied, Toast.LENGTH_SHORT).show()
                        }
                    )
            )
        }
        is ViewContent.TextContent -> {
            Text(
                text = classroom.content,
                fontSize = fontSize,
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
private fun textColor(background: Color): Color {
    val isLightColor = background.luminance() > 0.5f
    if ((!isSystemInDarkTheme() && isLightColor) || !isLightColor) {
        return MaterialTheme.colorScheme.onSurface
    }
    return MaterialTheme.colorScheme.surface
}