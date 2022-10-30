package com.example.vitrader.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import com.example.vitrader.utils.db.HistoryDatabase
import com.example.vitrader.utils.model.History
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HistoryManager {

    private val _histories = mutableStateListOf<History>()
    val histories get() = _histories

    @RequiresApi(Build.VERSION_CODES.O)
    fun makeHistory(symbol: String, price: Double, count: Double, transaction: String): History {
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formatted = date.format(formatter)
        return History(symbol = symbol,
            transaction = transaction,
            price = price,
            count = count,
            date = formatted)
    }

    fun addHistory(history: History, context: Context) {
        _histories.add(0, history)
        CoroutineScope(Dispatchers.IO).launch {
            HistoryDatabase.getDatabase(context)?.historyDao()?.insertHistory(history)
        }
    }

    suspend fun initializeHistories(context: Context) {
        val list = CoroutineScope(Dispatchers.IO).async {
            HistoryDatabase.getDatabase(context)?.historyDao()?.getAllHistories() ?: listOf()
        }.await()
        _histories.addAll(list.reversed())
    }

    fun getSymbolHistories(symbol: String): List<History> {
        val symbolHistories = mutableListOf<History>()

        for(h in histories)
            if(h.symbol == symbol)
                symbolHistories.add(h)

        return symbolHistories
    }
}