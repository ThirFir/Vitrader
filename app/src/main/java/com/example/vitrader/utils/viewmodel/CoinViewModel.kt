package com.example.vitrader.utils.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vitrader.utils.model.CoinRepository
import java.lang.IllegalArgumentException

class CoinViewModel(private val symbol: String) : ViewModel() {
    private val coinRepository = CoinRepository
    val coin get() = coinRepository.coins[symbol]
}

class CoinViewModelFactory(private val symbol: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(CoinViewModel::class.java))
            CoinViewModel(symbol) as T
        else
            throw IllegalArgumentException()
    }
}