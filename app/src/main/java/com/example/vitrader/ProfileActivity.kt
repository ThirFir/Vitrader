package com.example.vitrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.vitrader.screen.ProfileScreen
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel

class ProfileActivity : ComponentActivity()  {

    private lateinit var userAccountViewModel: UserAccountViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userAccountViewModel = ViewModelProvider(this)[UserAccountViewModel::class.java]
        userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]

        setContent{
            VitraderTheme {
                ProfileScreen(userAccountViewModel, userProfileViewModel)
            }
        }
    }
}