package com.example.vitrader.utils.model

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
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

    fun setProfileImg(bitmap: Bitmap) {
        _userProfileData.value = UserProfileData(bitmap, userProfileData.value.nickname)
        userRemoteDataSource.updateProfile(_userProfileData.value)
    }
    fun setNickname(nickname: String) {
        _userProfileData.value = UserProfileData(userProfileData.value.profileImg, nickname)
        userRemoteDataSource.updateProfile(_userProfileData.value)
    }
}