package com.example.vitrader.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object Rankers {

    private const val RANKER_COUNT = 100L

    private val _rankers = mutableStateListOf<Ranker>()
    private val _rankerProfileImage = mutableStateMapOf<String, Bitmap?>()
    private val _rankerNickname = mutableStateMapOf<String, String>()

    val rankers get() = _rankers
    val rankerProfileImage get() = _rankerProfileImage
    val rankerNickname get() = _rankerNickname

    init {
        loadRankers()
    }

    private fun loadRankers() {
        // 상위 RANKER_COUNT 명의 uid, krw 가져옴.
        val db = FirebaseFirestore.getInstance()
        db.collection("account").orderBy("krw").limit(RANKER_COUNT).get().addOnSuccessListener {
            if(it.metadata.isFromCache) return@addOnSuccessListener
            _rankers.clear()
            for(doc in it.documents) {
                val krw = doc["krw"] as Long
                val uid = doc.reference.id

                _rankers.add(Ranker(uid, krw))
                loadRankerProfileImage(uid)
                loadRankerNickname(uid)
            }

            _rankers.sortByDescending { rank -> rank.krw }

        }
    }

    private fun loadRankerProfileImage(uid: String) {
        if(_rankerProfileImage[uid] != null) return

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference(uid)

        storageRef.getBytes(100000000).addOnCompleteListener {
            if(it.isSuccessful) {
                val byteArray = it.result
                val bitmap: Bitmap? =
                    if (byteArray != null)
                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    else null
                _rankerProfileImage[uid] = bitmap
            }
        }

    }

    private fun loadRankerNickname(uid: String) {
        if(_rankerNickname[uid] != null) return

        val db = FirebaseFirestore.getInstance()
        db.collection("profile").document(uid).get().addOnCompleteListener {
            if(it.isSuccessful)
                _rankerNickname[uid] = it.result.get("nickname").toString()
            else
                _rankerNickname[uid] = "Unknown"

        }
    }
}