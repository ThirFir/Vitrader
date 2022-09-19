package com.example.vitrader.screen.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@Composable
fun RegisterInputScreen(navController: NavController?) {
    val context = LocalContext.current

    Surface(Modifier.fillMaxSize()) {
        Column() {
            var emailInput by remember { mutableStateOf("") }
            LoginTextField(inputText = emailInput, placeholder = "이메일 주소", isPassword = false) { emailInput = it }

            var passwordInput by remember { mutableStateOf("") }
            LoginTextField(inputText = passwordInput, placeholder = "비밀번호", isPassword = true) { passwordInput = it }

            Button(onClick = { registerUser(context, emailInput, passwordInput, navController) }) {
                Text("가입")
            }
        }
    }
}

private fun registerUser(context: Context, email: String, password: String, navController: NavController?) {
    if(email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "이메일 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        return
    }

    val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener() { task ->
            if(task.isSuccessful) {
                Log.d("회원가입 성공", "it")
                auth.currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener{ sendTask ->
                        if(sendTask.isSuccessful) {
                            Log.d("이메일 인증", "성공")
                            navController?.popBackStack()
                            navController?.navigate(LoginScreenDestination.LOGIN.route)
                        }
                        else
                            Log.d("이메일 인증", "실패")
                    }
            }
            else {
                Log.d("회원가입 실패", task.exception?.message!!)
            }
        }
}