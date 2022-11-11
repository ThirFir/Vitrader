package com.example.vitrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import com.example.vitrader.screen.ProfileScreen
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.ActivityManager
import com.example.vitrader.utils.db.UserRemoteDataSource
import com.example.vitrader.utils.model.UserAccountData
import com.example.vitrader.utils.model.UserProfileData
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity()  {

    private lateinit var userAccountViewModel: UserAccountViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var coinListViewModel: CoinListViewModel
    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.activities.add(this)

        uid = intent.getStringExtra("uid") ?: ""
        if(uid == "")   // uid 받아오기에 실패하면 액티비티 종료
            finish()

        val userAccount = mutableStateOf(UserAccountData())
        val userProfile = mutableStateOf(UserProfileData())
        userAccountViewModel = ViewModelProvider(this)[UserAccountViewModel::class.java]
        userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
        coinListViewModel = ViewModelProvider(this)[CoinListViewModel::class.java]

        if(uid == FirebaseAuth.getInstance().currentUser?.uid) {
            userAccount.value = UserAccountData(userAccountViewModel.krw, userAccountViewModel.possessingCoins, userAccountViewModel.totalBuy)
            userProfile.value = UserProfileData(userProfileViewModel.profileImage, userProfileViewModel.nickname, userProfileViewModel.bookmark)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                userAccount.value = UserRemoteDataSource.getUserAccountData(uid)
                userProfile.value = UserRemoteDataSource.getUserProfileData(uid)
            }
        }

        setContent{
            VitraderTheme {
                ProfileScreen(userAccount.value, userProfile.value, userProfileViewModel, coinListViewModel, uid)
            }
        }
    }

    override fun onDestroy() {
        ActivityManager.activities.remove(this)
        super.onDestroy()
    }
}