package com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun LongClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = false,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource,
    onClick: (annotation: AnnotatedString.Range<String>?) -> Unit,
    onLongClick: (annotation: AnnotatedString.Range<String>?) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val gesture = Modifier.pointerInput(onClick, onLongClick, interactionSource) {
        detectTapGestures(
            onLongPress = { pos ->
                layoutResult.value?.let { layout ->
                    val offset = layout.getOffsetForPosition(pos)
                    val annotation = text.getStringAnnotations(offset, offset).firstOrNull()
                    onLongClick(annotation)
                }
            },
            onTap = { pos ->
                layoutResult.value?.let { layout ->
                    val offset = layout.getOffsetForPosition(pos)
                    val annotation = text.getStringAnnotations(offset, offset).firstOrNull()
                    onClick(annotation)
                }
            },
            onPress = { pos ->
                val annotation = layoutResult.value?.let { layout ->
                    val offset = layout.getOffsetForPosition(pos)
                    text.getStringAnnotations(offset, offset).firstOrNull()
                }

                if (annotation == null) {
                    val press = PressInteraction.Press(pos)
                    interactionSource.emit(press)
                    awaitRelease()
                    interactionSource.emit(PressInteraction.Release(press))
                }
            }
        )
    }

    Text(
        text = text,
        modifier = modifier.then(gesture),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
        }
    )
}