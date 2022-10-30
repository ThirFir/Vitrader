package com.example.vitrader.utils.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.vitrader.utils.model.UserProfileRepository

class UserProfileViewModel: ViewModel() {
    private val userProfileRepository = UserProfileRepository
    private val userProfileData get() = userProfileRepository.userProfileData

    val profileImage get() = userProfileData.value.profileImg
    val nickname get() = userProfileData.value.nickname
    val bookmark get() = userProfileData.value.bookmark

    fun updateProfileImage(bitmap: Bitmap?) {
        if(bitmap != null)
            userProfileRepository.updateProfileImage(bitmap)
    }
    fun updateNickname(nickname: String) {
        userProfileRepository.updateNickname(nickname)
    }
    fun bookmark(symbol: String?) {
        if (symbol != null) {
            if(userProfileData.value.bookmark.contains(symbol))
                userProfileRepository.removeBookmark(symbol)
            else
                userProfileRepository.addBookmark(symbol)
        }

    }
}