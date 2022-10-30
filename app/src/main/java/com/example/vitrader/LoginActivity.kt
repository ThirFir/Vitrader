package com.example.vitrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.vitrader.navigation.LoginNavHost
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.ActivityManager

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.activities.add(this)

        setContent {
            VitraderTheme {
                val navHostController = rememberNavController()
                LoginNavHost(navHostController = navHostController)
            }
        }
    }

    override fun onDestroy() {
        ActivityManager.activities.remove(this)
        super.onDestroy()
    }
}
