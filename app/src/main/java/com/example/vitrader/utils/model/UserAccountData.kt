package com.example.vitrader.utils.model

import com.google.gson.annotations.SerializedName

data class UserAccountData(
    @SerializedName("krw")              var krw: Long = 0,                   //보유 krw
    @SerializedName("possessingCoins")  val possessingCoins: MutableMap<String, MutableMap<String, Double>> = mutableMapOf(),     // 보유 코인 ( Map<심볼, Map<매수가, 개수>> ) 단 매수가는 String 으로 받아오므로 사용 시 Double로 변경해야 함 , Double : %.8f
    @SerializedName("totalBuy")         var totalBuy: Long = 0,              //총 매수값
)
