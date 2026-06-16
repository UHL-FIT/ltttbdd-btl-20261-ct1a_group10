package com.example.qlct.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem("Trang chủ", Icons.Default.Home, "home")
    object Report : BottomNavItem("Báo cáo", Icons.Default.Assessment, "report")
    object Account : BottomNavItem("Tài khoản", Icons.Default.Person, "account")
    object More : BottomNavItem("Khác", Icons.Default.MoreHoriz, "more")
}
