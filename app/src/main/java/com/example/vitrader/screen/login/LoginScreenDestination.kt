package com.example.vitrader.screen.login

import androidx.compose.runtime.Composable

enum class LoginScreenDestination(val route: String, val description: String, val screen : @Composable () -> Unit){
    LOGIN("login", "login", { LoginScreen (null) }),

    REGISTER_INPUT("register_input", "register_input", { RegisterInputScreen(null) });

}
