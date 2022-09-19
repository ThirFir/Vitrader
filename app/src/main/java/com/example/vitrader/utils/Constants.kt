package com.example.vitrader.utils

import androidx.compose.ui.graphics.Color
import com.example.vitrader.utils.model.Coin
import com.example.vitrader.utils.viewmodel.UserViewModel
import java.text.DecimalFormat

object UpbitAPI {
    const val BASE_URL = "https://api.upbit.com/v1/"
    const val ALL_COIN_SUB_URL = "market/all"
    const val TICKER = "ticker?"
    const val MARKETS = "markets="
    //https://api.upbit.com/v1/ticker?markets=KRW-EOS

    const val WEB_SOCKET = "wss://api.upbit.com/websocket/v1"
}

fun userCoinCount(userViewModel: UserViewModel, symbol: String) : Double {
    return userViewModel.possessingCoins[symbol]?.values?.sum() ?: 0.0
}

fun dbDoubleFormat(value: Double) : Double = String.format("%.8f", value).toDouble()
