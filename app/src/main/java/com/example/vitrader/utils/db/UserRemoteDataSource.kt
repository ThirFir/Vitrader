package com.example.vitrader.utils.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import com.example.vitrader.utils.model.UserAccountData
import com.example.vitrader.utils.model.UserProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

object UserRemoteDataSource {

    private const val accountCollection = "account"
    private const val profileCollection = "profile"
    private const val TAG = "UserRemoteDataSource"

    private val auth = FirebaseAuth.getInstance()
    private val myUid = auth.currentUser?.uid!!

    private val db get() = FirebaseFirestore.getInstance()

    private const val initialKRW = 100000000L


    suspend fun createInitialCloudAndRealTimeData(uid: String, nickname: String) {
        createInitialAccountData(uid)
        createInitialProfileData(uid, nickname)
        delay(2000)
    }

    private suspend fun createInitialAccountData(uid: String) {
        val fields = hashMapOf(
            "krw" to initialKRW,
            "possessingCoins" to mutableMapOf<String, MutableMap<String, Double>>(),
            "totalBuy" to 0L,
        )

        /* if account document of uid does not exist,
           Create new account document. */
        if(!getAccountDocument(uid).exists()) {
            Log.d(TAG, "create account document $uid")
            db.collection(accountCollection).document(uid).set(fields)
        }
    }

    private suspend fun createInitialProfileData(uid: String, nickname: String) {
        val fields = hashMapOf(
            "nickname" to nickname,
            "bookmark" to mutableListOf<String>()
        )

        /* if profile document of uid does not exist,
           Create new account document. */
        if(!getProfileDocument(uid).exists()) {
            Log.d(TAG, "create profile document $uid")
            db.collection(profileCollection).document(uid).set(fields)
        }
    }

    /** Account document from Firebase Cloud.
     *
     *  path : account -> $uid
     *  */
    private suspend fun getAccountDocument(uid: String): DocumentSnapshot =
        CoroutineScope(Dispatchers.IO).async {
            db.collection(accountCollection).document(uid).get().await()
        }.await()

    /** Profile document from Firebase Cloud.
     *
     *  path : profile -> $uid
     *  */
    private suspend fun getProfileDocument(uid: String): DocumentSnapshot =
        CoroutineScope(Dispatchers.IO).async {
            db.collection(profileCollection).document(uid).get().await()
        }.await()


    suspend fun getUserAccountData(uid: String = myUid) : UserAccountData {

        // Firestore
        val userAccountData = try {
            getAccountDocument(uid).toObject(UserAccountData::class.java)
        }
        catch(e: FirebaseFirestoreException){
            return UserAccountData()
        }

        return userAccountData!!
    }

    suspend fun getUserProfileData(uid: String = myUid) : UserProfileData {

        // Firestore
        val userProfileData = try {
            getProfileDocument(uid).toObject(UserProfileData::class.java)
        }
        catch(e: FirebaseFirestoreException){
            return UserProfileData()
        }

        // Firebase storage
        try {
            userProfileData!!.profileImg = loadProfileImage(uid)
        }
        catch (e: StorageException) {
            // e.printStackTrace()
            return userProfileData!!
        }

        return userProfileData
    }

    /** Get user profile image from Firebase storage. */
    private suspend fun loadProfileImage(uid: String): Bitmap {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(uid)

        val byteArray = storageRef.getBytes(100000000).addOnSuccessListener {
            Log.d(TAG, "loadProfileImage : load success - uid : $uid")
        }.await()

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


    fun updateAccount(userAccountData: UserAccountData) {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(accountCollection).document(myUid)
                .apply {
                    update("krw", userAccountData.krw)
                    update("possessingCoins", userAccountData.possessingCoins)
                    update("totalBuy", userAccountData.totalBuy)
                }
        }
    }

    fun updateProfileImage(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            val storage = FirebaseStorage.getInstance()
            val storageProfileImgRef = storage.getReference(myUid)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            CoroutineScope(Dispatchers.IO).launch {
                storageProfileImgRef.putBytes(data)
            }
        }
    }
    fun updateNickname(nickname: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(profileCollection).document(myUid)
                .update("nickname", nickname)
        }
    }
    fun updateBookmark(bookmarkList: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            db.collection(profileCollection).document(myUid)
                .update("bookmark", bookmarkList)
        }
    }

}