package com.vereshchagin.nikolay.stankinschedule.schedule.ui.viewer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.PairModel
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.Type

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairCard(
    pair: PairModel,
    pairColors: PairColors,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimen.ContentPadding),
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
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = pair.time.startString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pair.time.endString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = pair.title,
                    fontSize = 16.sp,
                )

                FlowRow(
                    mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pair.lecturer,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = pair.classroom,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 14.sp,
                    )
                }

                FlowRow(
                    mainAxisSpacing = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val typeColor = when (pair.type) {
                        Type.LECTURE -> pairColors.lectureColor
                        Type.SEMINAR -> pairColors.seminarColor
                        Type.LABORATORY -> pairColors.laboratoryColor
                    }

                    Text(
                        text = pair.type.tag,
                        color = if (typeColor.luminance() > 0.5f) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        fontSize = 14.sp,
                        modifier = Modifier
                            .background(
                                color = typeColor,
                                shape = RoundedCornerShape(percent = 50)
                            )
                            .padding(4.dp)
                    )

                    val subgroupColor: Color? = when (pair.subgroup) {
                        Subgroup.COMMON -> null
                        Subgroup.A -> pairColors.subgroupAColor
                        Subgroup.B -> pairColors.subgroupBColor
                    }

                    if (subgroupColor != null) {
                        Text(
                            text = pair.subgroup.tag,
                            color = if (subgroupColor.luminance() > 0.5f) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
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
            }
        }
    }
}