package com.example.qlct.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.qlct.viewmodel.AuthViewModel
import com.example.qlct.viewmodel.TransactionViewModel

@Composable
fun MainScreen(authVM: AuthViewModel, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val userId = authVM.getUserId() // Lấy ID người dùng hiện tại
    val transactionVM: TransactionViewModel = viewModel()

    // Tải dữ liệu giao dịch và danh mục ngay khi vào ứng dụng
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            transactionVM.loadTransactions(userId)
            transactionVM.loadCategories(userId)
        }
    }

    Scaffold(
        bottomBar = {
            MainBottomNavigation(navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(authVM, transactionVM, userId)
            }
            composable(BottomNavItem.Report.route) {
                ReportScreen(transactionVM, userId)
            }
            composable(BottomNavItem.Account.route) {
                AccountScreen(authVM, onLogout)
            }
            composable(BottomNavItem.More.route) {
                MoreScreen(transactionVM, userId)
            }
        }
    }
}

@Composable
fun MainBottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Report,
        BottomNavItem.Account,
        BottomNavItem.More
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Tránh việc nhấn lại 1 tab tạo ra nhiều bản sao trên stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.title) }
            )
        }
    }
}
