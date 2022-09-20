package com.example.vitrader.utils.db

import com.example.vitrader.utils.dbDoubleFormat
import com.example.vitrader.utils.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToLong

object UserRemoteDataSource {

    private const val rootCollectionPath = "users"
    private val auth = FirebaseAuth.getInstance()
    private val userDocumentPath = auth.currentUser?.email.toString()

    suspend fun getUserDataFromDB() : UserData {
        val db = FirebaseFirestore.getInstance()
        val userData: UserData?

        val doc = CoroutineScope(Dispatchers.Default).async {
            db.collection(rootCollectionPath).document(userDocumentPath).get().await()
        }.await()


        userData = doc.toObject(UserData::class.java) ?: throw Exception("User Database calling exception - can't load data")
        return userData
    }

    suspend fun buy(symbol: String, price: Double, count: Double) : UserData {
        val userData = getUserDataFromDB()

        // About "possessingCoins"
        if(userData.possessingCoins[symbol] == null)
            userData.possessingCoins[symbol] = mutableMapOf()

        var averagePrice = price
        var possessCount = count

        val keyIterator = userData.possessingCoins[symbol]?.keys?.iterator()
        if(keyIterator?.hasNext() == true) {     // 이미 보유 중인 코인이면
            val prevAveragePrice = keyIterator.next()     // 기존 평균가
            val prevCount = userData.possessingCoins[symbol]?.get(prevAveragePrice) ?: 0.0      // 기존 보유 개수

            possessCount = prevCount + count

            averagePrice = (prevAveragePrice.toDouble() * prevCount + price * count) / possessCount     // 새 평균가

            userData.possessingCoins[symbol]?.remove(prevAveragePrice)
        }
        userData.possessingCoins[symbol]?.set(dbDoubleFormat(averagePrice).toString(), String.format("%.8f", possessCount).toDouble())      // 업데이트된 Map(평균가, 개수)

        val coinEvaluation = (price * count).toLong()
        // About "krw"
        userData.krw -= coinEvaluation
        if(userData.krw <= 1) userData.krw = 0

        // About "totalBuy"
        userData.totalBuy += coinEvaluation

        update(userData)

        return userData
    }

    suspend fun sell(symbol: String, price: Double, count: Double) : UserData {
        val userData = getUserDataFromDB()

        val keyIterator = userData.possessingCoins[symbol]?.keys?.iterator()
        if(keyIterator?.hasNext() == true) {
            val averagePrice = keyIterator.next()
            val prevCount = userData.possessingCoins[symbol]?.get(averagePrice) ?: 0.0

            userData.krw += (price * count).roundToLong()
            userData.totalBuy -= (averagePrice.toDouble() * count).roundToLong()
            if(userData.totalBuy < 0) userData.totalBuy = 0
            userData.possessingCoins[symbol]?.set(averagePrice,
                userData.possessingCoins[symbol]?.get(averagePrice)!! - count)

            if(userData.possessingCoins[symbol]?.get(averagePrice)!! < 0.00000001)
                userData.possessingCoins.remove(symbol)
        }


        update(userData)

        return userData
    }

    suspend fun updateBookmark(bookmarkList: List<String>) {
        val db = FirebaseFirestore.getInstance()
        db.collection(rootCollectionPath).document(userDocumentPath)
            .update("bookmark", bookmarkList)

    }

    fun update(userData: UserData) {
        val db = FirebaseFirestore.getInstance()
        db.collection(rootCollectionPath).document(userDocumentPath)
            .apply { update("krw", userData.krw)
                update("possessingCoins", userData.possessingCoins)
                update("totalBuy", userData.totalBuy)
                update("bookmark", userData.bookmark)
            }

    }


    suspend fun getUpdatedData(userData: UserData) : UserData {
        return UserData()
    }

    fun updatePossessingCoins(possessingCoins: MutableMap<String, MutableMap<String, Double>>) {
        val db = FirebaseFirestore.getInstance()
        db.collection(rootCollectionPath).document(userDocumentPath)
            .apply {
                update("possessingCoins", possessingCoins)
            }
    }
}