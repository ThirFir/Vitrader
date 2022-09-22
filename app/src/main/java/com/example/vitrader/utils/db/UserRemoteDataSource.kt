package com.example.vitrader.utils.db

import com.example.vitrader.utils.dbDoubleFormat
import com.example.vitrader.utils.model.UserData
import com.example.vitrader.utils.model.UserRepository.userData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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


    fun updateBookmark(bookmarkList: List<String>) {
        CoroutineScope(Dispatchers.Default).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection(rootCollectionPath).document(userDocumentPath)
                .update("bookmark", bookmarkList)
        }
    }

    fun update(userData: UserData) {
        CoroutineScope(Dispatchers.Default).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection(rootCollectionPath).document(userDocumentPath)
                .apply {
                    update("krw", userData.krw)
                    update("possessingCoins", userData.possessingCoins)
                    update("totalBuy", userData.totalBuy)
                    update("bookmark", userData.bookmark)
                }
        }
    }

}