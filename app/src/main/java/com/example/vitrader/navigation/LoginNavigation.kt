package com.example.vitrader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vitrader.screen.login.LoginScreen

import com.example.vitrader.screen.login.LoginScreenDestination
import com.example.vitrader.screen.login.RegisterInputScreen

@Composable
fun LoginNavHost(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = LoginScreenDestination.LOGIN.route){
        composable(LoginScreenDestination.LOGIN.route) {
            LoginScreen(navHostController)
        }
        composable(LoginScreenDestination.REGISTER_INPUT.route) {
            RegisterInputScreen(navHostController)
        }
    }
}