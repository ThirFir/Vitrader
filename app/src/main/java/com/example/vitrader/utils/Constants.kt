package com.example.vitrader.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.example.vitrader.utils.viewmodel.UserAccountViewModel

object UpbitAPI {
    const val BASE_URL = "https://api.upbit.com/v1/"
    const val ALL_COIN_SUB_URL = "market/all"
    const val TICKER = "ticker?"
    const val MARKETS = "markets="
    //https://api.upbit.com/v1/ticker?markets=KRW-EOS

    const val WEB_SOCKET = "wss://api.upbit.com/websocket/v1"
}

fun userCoinCount(userAccountViewModel: UserAccountViewModel, symbol: String) : Double {
    return userAccountViewModel.possessingCoins[symbol]?.values?.sum() ?: 0.0
}

fun dbDoubleFormat(value: Double) : Double = String.format("%.8f", value).toDouble()

@SuppressLint("UnnecessaryComposedModifier")
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}
