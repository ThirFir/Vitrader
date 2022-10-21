package com.example.vitrader.utils.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.vitrader.utils.model.History

@Database(entities = [History::class], version = 1)
abstract class HistoryDatabase: RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        private var INSTANCE: HistoryDatabase? = null

        fun getDatabase(context: Context): HistoryDatabase? {
            if(INSTANCE == null)
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    "history_database")
                    .build()

            return INSTANCE
        }
    }
}

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getAllHistories(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("SELECT * FROM history where symbol = :symbol")
    fun getSymbolHistory(symbol: String): List<History>
}