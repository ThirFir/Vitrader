package com.example.vitrader.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.vitrader.R
import com.example.vitrader.utils.viewmodel.UserAccountViewModel

object UpbitAPI {
    const val BASE_URL = "https://api.upbit.com/v1/"
    const val ALL_COIN_SUB_URL = "market/all"

    const val WEB_SOCKET = "wss://api.upbit.com/websocket/v1"
}


fun dbDoubleFormat(value: Double) : Double = String.format("%.8f", value).toDouble()

@SuppressLint("UnnecessaryComposedModifier")
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
fun AutoResizeText(text: String,
                   modifier: Modifier = Modifier,
                   color: Color = colorResource(id = R.color.white),
                   textAlign: TextAlign = TextAlign.Center,
                   textStyle: TextStyle? = null,
                   targetTextSizeHeight: TextUnit? = textStyle?.fontSize,
                   maxLines: Int = 1) {
    var textSize by remember { mutableStateOf(targetTextSizeHeight ?: 14.5.sp) }

    Text(
        modifier = modifier,
        text = text,
        color = color,
        textAlign = textAlign,
        fontSize = textSize,
        fontFamily = textStyle?.fontFamily,
        fontStyle = textStyle?.fontStyle,
        fontWeight = textStyle?.fontWeight,
        lineHeight = textStyle?.lineHeight ?: TextUnit.Unspecified,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { textLayoutResult ->
            val maxCurrentLineIndex: Int = textLayoutResult.lineCount - 1

            if (textLayoutResult.isLineEllipsized(maxCurrentLineIndex)) {
                textSize = textSize.times(0.9f)
            }
        },
    )
}