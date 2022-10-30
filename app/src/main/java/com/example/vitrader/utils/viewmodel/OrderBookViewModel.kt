package com.example.vitrader.utils.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vitrader.utils.model.OrderBookRepository
import java.lang.IllegalArgumentException

class OrderBookViewModel(private val symbol: String): ViewModel() {
    val orderBook get() = OrderBookRepository.orderBooks[symbol]
}

class OrderBookViewModelFactory(private val symbol: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(OrderBookViewModel::class.java))
            OrderBookViewModel(symbol) as T
        else
            throw IllegalArgumentException()
    }
}