package com.example.qlct.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qlct.viewmodel.TransactionViewModel
import com.example.qlct.util.formatCurrency
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportScreen(
    transactionVM: TransactionViewModel,
    userId: String
) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }












    val currentMonthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)

    val transactions = transactionVM.transactions.filter {
        val transCal = Calendar.getInstance().apply { timeInMillis = it.date }
        transCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                transCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
    }

    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val IncomeColor = Color(0xFF2E7D32) // Xanh lá đậm
    val ExpenseColor = Color(0xFFD32F2F) // Đỏ đậm

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Custom App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = {
                        calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tháng trước", tint = Color.White)
                    }
                    Text(
                        text = currentMonthName.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(onClick = {
                        calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Tháng sau", tint = Color.White)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Summary Box
                SummarySection(totalIncome, totalExpense, balance)
                
                Spacer(modifier = Modifier.height(16.dp))

                // Thẻ thống kê chi tiết
                DetailedStatsCard(transactions)

                Spacer(modifier = Modifier.height(16.dp))
                
                // New Comparison Card instead of the weird donut
                ExpenseIncomeRatioCard(totalIncome, totalExpense)
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Tab Selection
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Chi tiêu") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Thu nhập") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            val categoryData = if (selectedTabIndex == 0) {
                transactions.filter { it.type == "expense" }
                    .groupBy { it.category }
                    .mapValues { it.value.sumOf { t -> t.amount } }
                    .toList()
                    .sortedByDescending { it.second }
            } else {
                transactions.filter { it.type == "income" }
                    .groupBy { it.category }
                    .mapValues { it.value.sumOf { t -> t.amount } }
                    .toList()
                    .sortedByDescending { it.second }
            }

            if (categoryData.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Không có dữ liệu giao dịch",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                val totalForTab = if (selectedTabIndex == 0) totalExpense else totalIncome
                val tabColor = if (selectedTabIndex == 0) ExpenseColor else IncomeColor
                
                item {
                    // Biểu đồ tròn (Donut Style) phân bổ hạng mục chuyên nghiệp
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (selectedTabIndex == 0) "Phân bổ Chi tiêu" else "Phân bổ Thu nhập",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Pie Chart (Vòng tròn khuyết/Donut)
                                Box(
                                    modifier = Modifier.size(160.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CategoryPieChart(categoryData, totalForTab, selectedTabIndex == 0)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if(totalForTab > 0) "${((categoryData.firstOrNull()?.second ?: 0.0) / totalForTab * 100).toInt()}%" else "0%",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Black,
                                            color = getPieColor(0, selectedTabIndex == 0)
                                        )
                                        Text(
                                            text = categoryData.firstOrNull()?.first ?: "N/A",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            maxLines = 1
                                        )
                                    }
                                }
                                
                                // Legend list
                                Column(
                                    modifier = Modifier.padding(start = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    categoryData.take(6).forEachIndexed { index, pair ->
                                        LegendItem(
                                            label = pair.first,
                                            percentage = (pair.second / totalForTab * 100).toInt(),
                                            color = getPieColor(index, selectedTabIndex == 0)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                items(categoryData) { (category, amount) ->
                    CategoryProgressCard(
                        category = category,
                        amount = amount,
                        total = totalForTab,
                        color = tabColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Visual Bar Chart for Trends
                Text(
                    text = "Xu hướng 6 tháng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                TrendBarChart(transactionVM)
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetailedStatsCard(transactions: List<com.example.qlct.model.Transaction>) {
    val expenses = transactions.filter { it.type == "expense" }.map { it.amount }
    
    if (expenses.isEmpty()) return

    val maxExpense = expenses.maxOrNull() ?: 0.0
    val minExpense = expenses.minOrNull() ?: 0.0
    val avgExpense = expenses.average()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Thống kê chi tiêu chi tiết",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatMiniItem("Lớn nhất", maxExpense, Color(0xFFD32F2F))
                StatMiniItem("Trung bình", avgExpense, MaterialTheme.colorScheme.primary)
                StatMiniItem("Nhỏ nhất", minExpense, Color(0xFF2E7D32))
            }
        }
    }
}

@Composable
fun StatMiniItem(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(
            formatCurrency(amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun ExpenseIncomeRatioCard(income: Double, expense: Double) {
    val IncomeColor = Color(0xFF2E7D32)
    val ExpenseColor = Color(0xFFD32F2F)
    val ratio = if (income > 0) (expense / income).toFloat() else if (expense > 0) 1f else 0f
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tỷ lệ Chi tiêu / Thu nhập", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("${(ratio * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, color = if(ratio > 1) ExpenseColor else MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(IncomeColor.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(ratio.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(if (ratio > 0.9f) ExpenseColor else IncomeColor)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if(ratio > 1) "Cảnh báo: Chi tiêu vượt quá thu nhập!" else if(ratio > 0.8) "Lưu ý: Bạn đã tiêu gần hết thu nhập" else "Tình hình tài chính đang ổn định",
                style = MaterialTheme.typography.labelSmall,
                color = if(ratio > 0.8) ExpenseColor else MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun SummarySection(income: Double, expense: Double, balance: Double) {
    val IncomeColor = Color(0xFF2E7D32)
    val ExpenseColor = Color(0xFFD32F2F)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryItemVertical("THU NHẬP", income, IncomeColor)
            VerticalDivider(modifier = Modifier.height(40.dp).align(Alignment.CenterVertically))
            SummaryItemVertical("CHI TIÊU", expense, ExpenseColor)
        }
    }
}

@Composable
fun SummaryItemVertical(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun CategoryPieChart(categoryData: List<Pair<String, Double>>, total: Double, isExpense: Boolean) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 30.dp.toPx()
        if (total == 0.0) {
            drawArc(
                color = Color.LightGray.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
        } else {
            var startAngle = -90f
            categoryData.forEachIndexed { index, pair ->
                val sweepAngle = (pair.second / total * 360f).toFloat() * animatedProgress
                drawArc(
                    color = getPieColor(index, isExpense),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )

                // Thêm đường kẻ trắng phân tách
                if (categoryData.size > 1) {
                    drawArc(
                        color = Color.White,
                        startAngle = startAngle,
                        sweepAngle = 0.8f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                }

                startAngle += sweepAngle
            }
        }
    }
}

fun getPieColor(index: Int, isExpense: Boolean): Color {
    val colors = if (isExpense) {
        listOf(
            Color(0xFFD32F2F), // Red
            Color(0xFFF57C00), // Orange
            Color(0xFFC2185B), // Pink
            Color(0xFF7B1FA2), // Purple
            Color(0xFFE64A19), // Deep Orange
            Color(0xFF5D4037)  // Brown
        )
    } else {
        listOf(
            Color(0xFF2E7D32), // Green
            Color(0xFF00796B), // Teal
            Color(0xFF1976D2), // Blue
            Color(0xFF303F9F), // Indigo
            Color(0xFF0097A7), // Cyan
            Color(0xFF689F38)  // Light Green
        )
    }
    return colors[index % colors.size]
}

@Composable
fun LegendItem(label: String, percentage: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(140.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label, 
            style = MaterialTheme.typography.bodySmall, 
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun CategoryProgressCard(category: String, amount: Double, total: Double, color: Color) {
    val percentage = if (total > 0) (amount / total).toFloat() else 0f
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = category, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = formatCurrency(amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(color)
                )
            }
            Text(
                text = "${(percentage * 100).toInt()}% của tổng mục",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 4.dp).align(Alignment.End)
            )
        }
    }
}

@Composable
fun TrendBarChart(transactionVM: TransactionViewModel) {
    val IncomeColor = Color(0xFF2E7D32)
    val ExpenseColor = Color(0xFFD32F2F)
    
    // Lấy dữ liệu 6 tháng gần nhất (bao gồm tháng hiện tại)
    val chartData = remember(transactionVM.transactions.size) {
        (0..5).reversed().map { i ->
            val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)
            
            val mTransactions = transactionVM.transactions.filter {
                val tCal = Calendar.getInstance().apply { timeInMillis = it.date }
                tCal.get(Calendar.MONTH) == month && tCal.get(Calendar.YEAR) == year
            }
            
            val income = mTransactions.filter { it.type == "income" }.sumOf { it.amount }
            val expense = mTransactions.filter { it.type == "expense" }.sumOf { it.amount }
            
            Triple(income, expense, cal)
        }
    }
    
    // Tìm giá trị lớn nhất để tính tỉ lệ chiều cao, tối thiểu là 1.0 để tránh chia cho 0
    val maxInHistory = chartData.maxOfOrNull { maxOf(it.first, it.second) }?.coerceAtLeast(1.0) ?: 1.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        chartData.forEach { (income, expense, cal) ->
            // Tính toán chiều cao dựa trên tỉ lệ so với giá trị lớn nhất
            // Nếu giá trị là 0 thì chiều cao là 0, nếu có giá trị thì tối thiểu 4dp để dễ nhìn
            val incomeHeight = if (income > 0) ((income / maxInHistory) * 150).coerceIn(4.0, 150.0).dp else 0.dp
            val expenseHeight = if (expense > 0) ((expense / maxInHistory) * 150).coerceIn(4.0, 150.0).dp else 0.dp
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(incomeHeight)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(IncomeColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(expenseHeight)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(ExpenseColor)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = SimpleDateFormat("MM/yy", Locale.getDefault()).format(cal.time),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
