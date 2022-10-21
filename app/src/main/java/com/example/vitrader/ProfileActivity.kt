package com.example.vitrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import com.example.vitrader.screen.ProfileScreen
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.db.UserRemoteDataSource
import com.example.vitrader.utils.model.UserAccountData
import com.example.vitrader.utils.model.UserProfileData
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity()  {

    private lateinit var userAccountViewModel: UserAccountViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        email = intent.getStringExtra("email") ?: ""
        if(email == "")
            email = FirebaseAuth.getInstance().currentUser?.email.toString()

        val userAccount = mutableStateOf(UserAccountData())
        val userProfile = mutableStateOf(UserProfileData())

        CoroutineScope(Dispatchers.IO).launch {
            userAccount.value = UserRemoteDataSource.getUserAccountData(email)
            userProfile.value = UserRemoteDataSource.getUserProfileData(email)
        }

        //userAccountViewModel = ViewModelProvider(this)[UserAccountViewModel::class.java]
        //userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]

        setContent{
            VitraderTheme {
                ProfileScreen(userAccount.value, userProfile.value)
            }
        }
    }
}