package com.example.vitrader.utils.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class History(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "transaction")   // 매수 or 매도
    val transaction: String,        // BUY or SELL

    @ColumnInfo(name = "price")     // 매수 가격
    val price: Double,

    @ColumnInfo(name = "count")     // 개수
    val count: Double,

    @ColumnInfo(name = "date")      // 거래 시각
    val date: String
)