package com.example.qlct

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.qlct.screen.AuthScreen
import com.example.qlct.screen.MainScreen
import com.example.qlct.ui.theme.QLCTTheme
import com.example.qlct.viewmodel.AuthViewModel

import com.example.qlct.util.CurrencyPrefs

class MainActivity : ComponentActivity() {
    private val TAG = "QLCT_Lifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Ứng dụng khởi tạo")
        CurrencyPrefs.init(this)
        val authVM = AuthViewModel()

        setContent {
            QLCTTheme {
                var isLoggedIn by remember { mutableStateOf(authVM.isLoggedIn()) }

                if (isLoggedIn) {
                    MainScreen(
                        authVM = authVM,
                        onLogout = {
                            authVM.logout()
                            isLoggedIn = false
                        }
                    )
                } else {
                    AuthScreen(
                        vm = authVM,
                        onLoginSuccess = {
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Ứng dụng bắt đầu hiển thị")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Người dùng bắt đầu tương tác")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Ứng dụng bị tạm dừng")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Ứng dụng không còn hiển thị")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Ứng dụng bị hủy")
    }
}