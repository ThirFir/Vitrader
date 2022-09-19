package com.example.vitrader.utils.model

import com.google.gson.annotations.SerializedName

data class Coin(val info: Info, var ticker: Ticker) {
    data class Info(
        @SerializedName("korean_name") val name: String,
        @SerializedName("market") val symbol: String,
    )
    data class Ticker(
        @SerializedName("code") var market: String,
        @SerializedName("trade_price") var trade_price: Double = 0.0,          // 현재가
        @SerializedName("opening_price") var opening_price: Double,            // 시작가
        @SerializedName("high_price") var high_price: Double,                  // 고가
        @SerializedName("low_price") var low_price: Double,                    // 저가
        @SerializedName("prev_closing_price") var prev_closing_price: Double,  // 전일 종가
        @SerializedName("change") var change: String,                          // 전일 대비 (RISE, EVEN, FALL)
        @SerializedName("change_price") var change_price: Double,              // 전일 대비 값
        @SerializedName("signed_change_price") var signed_change_price: Double,              // 전일 대비 값
        @SerializedName("change_rate") var change_rate: Double,                // 전일 대비 등락율
        @SerializedName("signed_change_rate") var signed_change_rate: Double,  // 부호 있는 전일 대비 등락율
        @SerializedName("trade_volume") var trade_volume: Double,              // 가장 최근 거래량
        @SerializedName("acc_trade_volume") var acc_trade_volume: Double,      // 누적 거래량(UTC 0시 기준)
        @SerializedName("acc_trade_volume_24h") var acc_trade_volume_24h: Double,  // 24시간 누적 거래량
        @SerializedName("acc_trade_price") var acc_trade_price: Double,      // 누적 거래대금(UTC 0시 기준)
        @SerializedName("acc_trade_price_24h") var acc_trade_price_24h: Double,  // 24시간 누적 거래대금
        @SerializedName("trade_date") var trade_date: String,                  // 최근 거래 일자(UTC) yyyyMMdd
        @SerializedName("trade_time") var trade_time: String,                  // 최근 거래 시작(UTC) HHmmss
        @SerializedName("ask_bid") var ask_bid: String,                        // 매수/매도 구분 (ASK : 매도, BID : 매수)
        @SerializedName("acc_ask_volume") var acc_ask_volume: Double,          // 누적 매도량
        @SerializedName("acc_bid_volume") var acc_bid_volume: Double,          // 누적 매수량
        @SerializedName("highest_52_week_price") var highest_52_week_price: Double,    // 52주 최고가
        @SerializedName("highest_52_week_date") var highest_52_week_date: String,  // 52주 최고가 달성일 yyyy-MM-dd
        @SerializedName("lowest_52_week_price") var lowest_52_week_price: Double,  // 52주 최저가
        @SerializedName("lowest_52_week_date") var lowest_52_week_date: String,    // 52주 최저가 달성일 yyyy-MM-dd
    )
}