package com.example.vitrader.utils.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.vitrader.utils.model.UserProfileRepository

class UserProfileViewModel: ViewModel() {
    private val userProfileRepository = UserProfileRepository
    private val userProfileData get() = userProfileRepository.userProfileData

    val profileImg get() = userProfileData.value.profileImg
    val nickname get() = userProfileData.value.nickname

    fun setProfileImg(bitmap: Bitmap?) {
        if(bitmap != null)
            userProfileRepository.setProfileImg(bitmap)
    }
    fun setNickname(nickname: String) {
        userProfileRepository.setNickname(nickname)
    }
}