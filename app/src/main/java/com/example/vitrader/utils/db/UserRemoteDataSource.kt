package com.example.vitrader.utils.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.vitrader.utils.model.UserAccountData
import com.example.vitrader.utils.model.UserProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
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

    private const val initialKRW = 100000000L

    private fun getFileNameFormat(email: String): String = email.replace(".", "_")
    private fun getEmailFormat(file: String): String = file.replace("_", ".")

    suspend fun createInitialCloudAndRealTimeData(email: String) {
        createInitialAccountData(email)
        createInitialRankData(email)
    }

    private suspend fun createInitialAccountData(email: String) {
        val db = FirebaseFirestore.getInstance()

        val fields = hashMapOf(
            "krw" to initialKRW,
            "possessingCoins" to mutableMapOf<String, MutableMap<String, Double>>(),
            "totalBuy" to 0L,
            "bookmark" to mutableListOf<String>(),
            "nickname" to email.split("@")[0]
        )

        val doc = getDocument(email)    // Coroutine
        if(doc == null)
            db.collection(rootCollectionPath).document(email).set(fields)

    }
    private suspend fun createInitialRankData(email: String) {
        val emailTrans = getFileNameFormat(email)
        val realTimeDoc = CoroutineScope(Dispatchers.IO).async {
            realTimeDBRef.child(emailTrans).get().await()
        }.await()

        if(realTimeDoc.value == null) {
            Log.d("realTimeDoc", "if")
            realTimeDBRef.child(emailTrans).setValue(initialKRW)
        }
        else{
            Log.d("realTimeDoc", "else : ${realTimeDoc.value}")

        }
    }

    /** Document from Firebase Cloud.
     *
     *  path : users -> $email
     *  */
    private suspend fun getDocument(email: String = userDocumentPath): DocumentSnapshot? {
        val db = FirebaseFirestore.getInstance()
        return CoroutineScope(Dispatchers.IO).async {
            db.collection(rootCollectionPath).document(getEmailFormat(email)).get().await()
        }.await()
    }

    suspend fun getUserAccountData(email: String = userDocumentPath) : UserAccountData {
        val db = FirebaseFirestore.getInstance()
        val userAccountData: UserAccountData?

        val doc = getDocument(email)!!
        userAccountData = doc.toObject(UserAccountData::class.java) ?: throw Exception("User Database calling exception - failed load data $email")
        return userAccountData
    }

    suspend fun getUserProfileData(email: String = userDocumentPath) : UserProfileData {

        val bitmap = getProfileImage()

        val db = FirebaseFirestore.getInstance()
        val userProfileData: UserProfileData?

        val doc = getDocument(email)!!
        userProfileData = doc.toObject(UserProfileData::class.java) ?: throw Exception("User Database calling exception - failed load data")
        userProfileData.profileImg = bitmap

        return userProfileData
    }

    suspend fun getProfileImage(email: String = userDocumentPath): Bitmap? {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(getFileNameFormat(email))

        val byteArray = storageRef.getBytes(100000000).addOnCompleteListener {
            if(it.isSuccessful) {

            }
        }.await()

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
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
            realTimeDBRef.child(getFileNameFormat(userDocumentPath)).setValue(userAccountData.krw)
        }
    }

    fun updateProfile(userProfileData: UserProfileData) {
        CoroutineScope(Dispatchers.Default).launch {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child(auth.currentUser?.email!!.split("@")[0])

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