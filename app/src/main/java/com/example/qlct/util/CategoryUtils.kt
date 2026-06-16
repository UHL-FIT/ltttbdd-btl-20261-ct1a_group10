package com.example.qlct.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun getCategoryIcon(name: String): ImageVector {
    return when (name) {
        "Lương" -> Icons.Default.Payments
        "Thưởng" -> Icons.Default.CardGiftcard
        "Kinh doanh" -> Icons.Default.Store
        "Đầu tư" -> Icons.AutoMirrored.Filled.TrendingUp
        "Ăn uống" -> Icons.Default.Restaurant
        "Di chuyển" -> Icons.Default.DirectionsCar
        "Mua sắm" -> Icons.Default.ShoppingBag
        "Giải trí" -> Icons.Default.SportsEsports
        "Nhà cửa" -> Icons.Default.Home
        "Sức khỏe" -> Icons.Default.Favorite
        "Giáo dục" -> Icons.Default.School
        else -> Icons.Default.Category
    }
}

fun getCategoryColor(name: String, isIncome: Boolean): Color {
    return when (name) {
        "Lương" -> Color(0xFF4CAF50)
        "Thưởng" -> Color(0xFFFFC107)
        "Kinh doanh" -> Color(0xFF2196F3)
        "Đầu tư" -> Color(0xFF9C27B0)
        "Ăn uống" -> Color(0xFFFF9800)
        "Di chuyển" -> Color(0xFF03A9F4)
        "Mua sắm" -> Color(0xFFE91E63)
        "Giải trí" -> Color(0xFF9C27B0)
        "Nhà cửa" -> Color(0xFF795548)
        "Sức khỏe" -> Color(0xFFF44336)
        "Giáo dục" -> Color(0xFF3F51B5)
        else -> if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F)
    }
}
