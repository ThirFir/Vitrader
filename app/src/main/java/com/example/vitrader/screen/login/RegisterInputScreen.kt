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
import com.example.vitrader.utils.db.UserRemoteDataSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterInputScreen(navController: NavController?) {
    val context = LocalContext.current

    Surface(Modifier.fillMaxSize()) {
        Column() {
            var emailInput by remember { mutableStateOf("") }
            LoginTextField(inputText = emailInput, placeholder = "이메일 주소", isPassword = false) { emailInput = it }

            var passwordInput by remember { mutableStateOf("") }
            LoginTextField(inputText = passwordInput, placeholder = "비밀번호", isPassword = true) { passwordInput = it }

            Button(onClick = {
                if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(context, "이메일 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                registerUser(emailInput, passwordInput, navController) { success, error ->
                    if (success) {
                        Toast.makeText(context, "회원가입 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("Register", "실패 : " + error!!)
                        Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("가입")
            }
        }
    }
}

private fun registerUser(
    email: String,
    password: String,
    navController: NavController?,
    onCompleted: (Boolean, String?) -> Unit,
) {

    val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                auth.currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener { sendTask ->
                        if (sendTask.isSuccessful) {

                            CoroutineScope(Dispatchers.IO).launch {
                                UserRemoteDataSource.createInitialCloudAndRealTimeData(auth.currentUser!!.email!!)
                            }

                            navController?.popBackStack()
                            navController?.navigate(LoginScreenDestination.LOGIN.route)

                            onCompleted(true, "")
                        } else
                            onCompleted(false, sendTask.exception?.message)

                    }
            } else
                onCompleted(false, task.exception?.message)

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