package com.example.vitrader.utils.model

import androidx.compose.runtime.mutableStateMapOf
import com.example.vitrader.utils.db.UpbitWebSocketListener2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object OrderBookRepository {

    private var _orderBooks = mutableStateMapOf<String, OrderBook>()
    val orderBooks get() = _orderBooks


    init {
        launchGettingExternalCoinData()
    }
    fun launchGettingExternalCoinData() {
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.Default).launch {
                UpbitWebSocketListener2.launchGettingExternalCoinData()
            }
        }
    }

    fun addOrderBook(orderBook: OrderBook) {
        _orderBooks[orderBook.symbol] = orderBook
    }
    fun updateOrderBook(orderBook: OrderBook) {
        _orderBooks[orderBook.symbol] = orderBook
    }
}