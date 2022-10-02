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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
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

                            val fields = hashMapOf(
                                "krw" to 0L,
                                "possessingCoins" to mutableMapOf<String, MutableMap<String, Double>>(),
                                "totalBuy" to 0L,
                                "bookmark" to mutableListOf<String>()
                            )

                            val realTimeDB = FirebaseDatabase.getInstance("https://vitrader-a8d28-default-rtdb.asia-southeast1.firebasedatabase.app")
                            val realTimeDBRef = realTimeDB.getReference("krw")
                            realTimeDBRef.child(email.split("@")[0]).setValue(0)

                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(auth.currentUser?.email.toString()).set(fields)
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
fun registerTestUser(context: Context) {
    val email = "test" + (1..100000).random() + "@gmail.com"
    val password = "123456"

    val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener() { task ->
            if(task.isSuccessful) {
                Log.d("회원가입 성공", "it")
                auth.currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener{ sendTask ->
                        if(sendTask.isSuccessful) {
                            Log.d("이메일 인증", "성공")

                            val randomKRW = (0L..1000000000L).random()
                            val fields = hashMapOf(
                                "krw" to randomKRW,
                                "possessingCoins" to mutableMapOf<String, MutableMap<String, Double>>(),
                                "totalBuy" to 0L,
                                "bookmark" to mutableListOf<String>(),
                                "profileImg" to "",
                                "nickname" to email.split("@")[0]
                            )

                            val realTimeDB = FirebaseDatabase.getInstance("https://vitrader-a8d28-default-rtdb.asia-southeast1.firebasedatabase.app")
                            val realTimeDBRef = realTimeDB.getReference("krw")
                            realTimeDBRef.child(email.split("@")[0]).setValue(randomKRW)

                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(auth.currentUser?.email.toString()).set(fields)

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