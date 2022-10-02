package com.example.vitrader.utils.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.vitrader.screen.bitmapToString
import com.example.vitrader.screen.stringToBitmap
import com.example.vitrader.utils.model.UserAccountData
import com.example.vitrader.utils.model.UserAccountRepository.userAccountData
import com.example.vitrader.utils.model.UserProfileData
import com.example.vitrader.utils.model.UserProfileRepository.userProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

object UserRemoteDataSource {

    private const val rootCollectionPath = "users"
    private val auth = FirebaseAuth.getInstance()
    private val userDocumentPath = auth.currentUser?.email.toString()
    private val realTimeDB = FirebaseDatabase.getInstance("https://vitrader-a8d28-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val realTimeDBRef = realTimeDB.getReference("krw")

    suspend fun getUserAccountData() : UserAccountData {
        val db = FirebaseFirestore.getInstance()
        val userAccountData: UserAccountData?

        val doc = CoroutineScope(Dispatchers.Default).async {
            db.collection(rootCollectionPath).document(userDocumentPath).get().await()
        }.await()

        userAccountData = doc.toObject(UserAccountData::class.java) ?: throw Exception("User Database calling exception - failed load data")
        return userAccountData
    }

    suspend fun getUserProfileData() : UserProfileData {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(auth.currentUser?.email!!)

        val byteArray = storageRef.getBytes(100000000).addOnCompleteListener {
            if(it.isSuccessful) {

            }
        }.await()

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        val db = FirebaseFirestore.getInstance()
        val userProfileData: UserProfileData?

        val doc = CoroutineScope(Dispatchers.Default).async {
            db.collection(rootCollectionPath).document(userDocumentPath).get().await()
        }.await()

        userProfileData = doc.toObject(UserProfileData::class.java) ?: throw Exception("User Database calling exception - failed load data")
        userProfileData.profileImg = bitmap

        return userProfileData
    }

    fun updateBookmark(bookmarkList: List<String>) {
        CoroutineScope(Dispatchers.Default).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection(rootCollectionPath).document(userDocumentPath)
                .update("bookmark", bookmarkList)
        }
    }

    fun updateAccount(userAccountData: UserAccountData) {
        CoroutineScope(Dispatchers.Default).launch {
            val db = FirebaseFirestore.getInstance()
            db.collection(rootCollectionPath).document(userDocumentPath)
                .apply {
                    update("krw", userAccountData.krw)
                    update("possessingCoins", userAccountData.possessingCoins)
                    update("totalBuy", userAccountData.totalBuy)
                    update("bookmark", userAccountData.bookmark)
                }
            realTimeDBRef.child(userDocumentPath.split("@")[0]).setValue(userAccountData.krw)
        }
    }

    fun updateProfile(userProfileData: UserProfileData) {
        CoroutineScope(Dispatchers.Default).launch {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child(auth.currentUser?.email!!)

            val baos = ByteArrayOutputStream()
            if(userProfileData.profileImg != null)
                userProfileData.profileImg!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            storageRef.putBytes(data).addOnCompleteListener{
                if(it.isSuccessful) {

                }
            }
        }
    }

}