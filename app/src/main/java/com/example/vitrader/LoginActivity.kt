package com.example.vitrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.vitrader.navigation.LoginNavHost
import com.example.vitrader.theme.VitraderTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VitraderTheme {
                val navHostController = rememberNavController()
                LoginNavHost(navHostController = navHostController)
            }
        }
    }
}
