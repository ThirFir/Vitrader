package com.example.vitrader.utils.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class UserProfileData(
    @SerializedName("profileImg") var profileImg: Bitmap? = null,
    @SerializedName("nickname") var nickname: String = ""
)
