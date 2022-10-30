package com.example.vitrader.utils.model

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import com.example.vitrader.utils.db.UserRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UserProfileRepository {
    private val userRemoteDataSource = UserRemoteDataSource
    private var _userProfileData = mutableStateOf(UserProfileData())
    val userProfileData get() = _userProfileData

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _userProfileData.value = userRemoteDataSource.getUserProfileData()
        }
    }

    fun updateProfileImage(bitmap: Bitmap) {
        _userProfileData.value = UserProfileData(bitmap, userProfileData.value.nickname)
        userRemoteDataSource.updateProfileImage(bitmap)
    }
    fun updateNickname(nickname: String) {
        _userProfileData.value = UserProfileData(userProfileData.value.profileImg, nickname)
        userRemoteDataSource.updateNickname(nickname)
    }
    fun addBookmark(symbol: String) {
        _userProfileData.value.bookmark.add(symbol)
        userRemoteDataSource.updateBookmark(userProfileData.value.bookmark)
    }
    fun removeBookmark(symbol: String) {
        _userProfileData.value.bookmark.remove(symbol)
        userRemoteDataSource.updateBookmark(userProfileData.value.bookmark)
    }
}