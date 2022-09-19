package com.example.vitrader.utils

import androidx.compose.ui.graphics.Color
import java.lang.Math.abs
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.roundToLong

object NumberFormat {
    fun coinPrice(price: Double) : String {
        return if(abs(price) >= 100)
            krwFormat(price.roundToLong())
        else if (abs(price) >= 1.0) String.format("%.2f", price)
        else String.format("%.4f", price)
    }
    fun coinRate(rate: Double) : String {
        return String.format("%.2f", rate * 100) + "%"
    }
    fun coinVolume(volume: Double) : String {
        return (volume / 1000000).toInt().toString() + "백만"
    }
    fun color(change: String) : Color {
        return when(change) {
            "RISE" -> Color.Green
            "FALL" -> Color.Red
            else -> Color.White
        }
    }
    fun krwFormat(long: Long) : String = DecimalFormat("#,###").format(BigDecimal(long).setScale(0, BigDecimal.ROUND_HALF_UP).toLong())
    fun krwFormat(double: Double) : String = DecimalFormat("#,###").format(BigDecimal(double).setScale(0, BigDecimal.ROUND_HALF_UP).toLong())
    fun doubleFormat(double: Double) : String = DecimalFormat("#,###").format(BigDecimal(double).setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString())
}

object SymbolFormat {
    fun get(symbol: String) : String {
        return symbol.substring(4 until symbol.length)
    }
}