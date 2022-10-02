package com.example.vitrader.screen.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vitrader.LoginActivity
import com.example.vitrader.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(navController: NavController?) {

    val context = LocalContext.current
    val auth = Firebase.auth
    tryAutoLogin(context, auth)

    Surface(Modifier.fillMaxSize()) {
        Column {
            var emailInput by remember { mutableStateOf( "" ) }
            LoginTextField(inputText = emailInput, placeholder = "이메일 주소", isPassword = false) { emailInput = it }

            var passwordInput by remember { mutableStateOf( "" ) }
            LoginTextField(inputText = passwordInput, placeholder = "비밀번호", isPassword = true) { passwordInput = it }

            Button(onClick = { tryLogin(context, emailInput, passwordInput) } ) {
                Text("로그인")
            }
            Button(onClick = { navController?.navigate(LoginScreenDestination.REGISTER_INPUT.route) }) {
                Text("회원가입")
            }
            Spacer(Modifier.size(100.dp))
            Button(onClick = { registerTestUser(context)} ){
                Text("테스트")
            }
        }
    }
}

private fun tryAutoLogin(context: Context, auth: FirebaseAuth) {
    if(auth.currentUser != null) {
        context.startActivity(Intent(context, MainActivity::class.java))
        (context as LoginActivity).finish()
    }
}

private fun tryLogin(context: Context, email: String, password: String) {
    if(email.isEmpty() || password.isEmpty())
        return

    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener() {
            if (it.isSuccessful) {
                Log.d("LogIn", "로그인 성공")
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as LoginActivity).finish()
            } else {
                Log.d("LogIn", "로그인 실패")
            }
        }
}

@Composable
internal fun LoginTextField(inputText: String, placeholder: String, isPassword: Boolean , onGettingInput: (String) -> Unit) {
    TextField(value = inputText,
        onValueChange =
        {
            onGettingInput(it)
        },
        singleLine = true, textStyle = MaterialTheme.typography.body1,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background),
        visualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background))

}
