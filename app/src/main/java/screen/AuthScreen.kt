package com.example.qlct.screen

import androidx.compose.runtime.*
import com.example.qlct.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    vm: AuthViewModel,
    onLoginSuccess: () -> Unit
) {

    var isLogin by remember {
        mutableStateOf(true)
    }

    if (isLogin) {

        LoginScreen(
            vm = vm,
            onLoginSuccess = onLoginSuccess,
            onGoRegister = {
                isLogin = false
            }
        )

    } else {

        RegisterScreen(
            vm = vm,
            onRegisterSuccess = {
                isLogin = true
            },
            onGoLogin = {
                isLogin = true
            }
        )

    }
}