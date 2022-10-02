package com.example.vitrader.utils.model

import com.google.gson.annotations.SerializedName

data class Candle(
    @SerializedName("candle_date_time_kst") val candle_date_time_kst: String,
    @SerializedName("opening_price") val opening_price: Float,
    @SerializedName("high_price") val high_price: Float,
    @SerializedName("low_price") val low_price: Float,
    @SerializedName("trade_price") val trade_price: Float,     // 종가
    @SerializedName("candle_acc_trade_price") val candle_acc_trade_price: Float,
    @SerializedName("candle_acc_trade_volume") val candle_acc_trade_volume: Float
)