package com.example.vitrader.utils.model

import com.google.gson.annotations.SerializedName

data class OrderBook(           // 호가 정보
    @SerializedName("code") val symbol: String,
    @SerializedName("orderbook_units") val units: List<Map<String, Double>>,
//    @SerializedName("ask_price") val ask_price: Double,
//    @SerializedName("bid_price") val bid_price: Double,
//    @SerializedName("ask_size") val ask_size: Double,
//    @SerializedName("bid_size") val bid_size: Double
)