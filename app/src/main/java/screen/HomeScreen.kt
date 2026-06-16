package com.example.qlct.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.qlct.viewmodel.AuthViewModel
import com.example.qlct.viewmodel.TransactionViewModel
import com.example.qlct.util.formatCurrency
import com.example.qlct.util.getCategoryIcon
import com.example.qlct.util.getCategoryColor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    authVM: AuthViewModel,
    transactionVM: TransactionViewModel,
    userId: String
) {
    // 1. Tự động tải dữ liệu từ Firebase khi vào màn hình
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            transactionVM.loadTransactions(userId)
            transactionVM.loadCategories(userId)
        }
    }

    val transactions = transactionVM.transactions

    var showAddMenu by remember { mutableStateOf(false) }
    var showIncome by remember { mutableStateOf(false) }
    var showExpense by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<com.example.qlct.model.Transaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<com.example.qlct.model.Transaction?>(null) }

    val totalIncome = transactionVM.getTotalIncome()
    val totalExpense = transactionVM.getTotalExpense()
    val balance = transactionVM.getBalance()

    val incomeColor = Color(0xFF2E7D32) // Xanh lá đậm
    val expenseColor = Color(0xFFD32F2F) // Đỏ đậm

    Scaffold(
        floatingActionButton = {
            Box(contentAlignment = Alignment.BottomEnd) {
                FloatingActionButton(
                    onClick = { showAddMenu = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm mới")
                }

                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { showAddMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Thêm Thu nhập") },
                        onClick = {
                            showAddMenu = false
                            showIncome = true
                        },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = incomeColor) }
                    )
                    DropdownMenuItem(
                        text = { Text("Thêm Chi tiêu") },
                        onClick = {
                            showAddMenu = false
                            showExpense = true
                        },
                        leadingIcon = { Icon(Icons.Default.Remove, contentDescription = null, tint = expenseColor) }
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 16.dp)
        ) {
            // Header: Tổng quan tài chính (Trả về thiết kế ban đầu, bỏ gấu trúc)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Xin chào,",
                                        color = Color.White.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = authVM.getDisplayName(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Số dư tổng cộng",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = formatCurrency(balance),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SummaryMiniCard(
                                    label = "Thu nhập",
                                    amount = totalIncome,
                                    icon = Icons.Default.ArrowUpward,
                                    contentColor = Color(0xFFE8F5E9)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(Color.White.copy(alpha = 0.2f))
                                )
                                SummaryMiniCard(
                                    label = "Chi tiêu",
                                    amount = totalExpense,
                                    icon = Icons.Default.ArrowDownward,
                                    contentColor = Color(0xFFFFEBEE)
                                )
                            }
                        }
                    }
                }
            }

            // Statistical Chart Area (Mini Bar Chart)
            item {
                if (totalIncome > 0 || totalExpense > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.3f
                            )
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Tỷ lệ Thu nhập / Chi tiêu",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val total = totalIncome + totalExpense
                            val incomeRatio = if (total > 0) (totalIncome / total).toFloat() else 0.5f
                            val incomePct = (incomeRatio * 100).roundToInt()
                            val expensePct = if (total > 0) 100 - incomePct else 0

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(CircleShape)
                                    .background(expenseColor.copy(alpha = 0.2f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .background(expenseColor)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(incomeRatio)
                                        .fillMaxHeight()
                                        .background(incomeColor)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(incomeColor)
                                    )
                                    Text(
                                        text = " $incomePct% Thu",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(expenseColor)
                                    )
                                    Text(
                                        text = " $expensePct% Chi",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Giao dịch gần đây",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Danh sách giao dịch
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val sortedTransactions = transactions.sortedByDescending { it.date }
            items(sortedTransactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onEdit = { editingTransaction = transaction },
                    onDelete = { transactionToDelete = transaction },
                    sdf = sdf
                )
            }
        }
    }

    // Full screen dialogs for Add/Edit
    if (editingTransaction != null) {
        Dialog(
            onDismissRequest = { editingTransaction = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                val transaction = editingTransaction!!
                AddTransactionScreen(
                    transactionVM = transactionVM,
                    userId = userId,
                    isIncome = transaction.type == "income",
                    initialAmount = transaction.amount,
                    initialCategory = transaction.category,
                    initialNote = transaction.note,
                    initialDate = transaction.date,
                    onBack = { editingTransaction = null }
                ) { amount, category, note, date ->
                    val updated = transaction.copy(
                        amount = amount,
                        category = category,
                        note = note,
                        date = date
                    )
                    transactionVM.updateTransaction(userId, updated)
                    editingTransaction = null
                }
            }
        }
    }

    if (showIncome) {
        Dialog(
            onDismissRequest = { showIncome = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                AddTransactionScreen(
                    transactionVM = transactionVM,
                    userId = userId,
                    isIncome = true,
                    onBack = { showIncome = false }
                ) { amount, category, note, date ->
                    transactionVM.addIncome(userId, amount, category, note, date)
                    showIncome = false
                }
            }
        }
    }

    if (showExpense) {
        Dialog(
            onDismissRequest = { showExpense = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                AddTransactionScreen(
                    transactionVM = transactionVM,
                    userId = userId,
                    isIncome = false,
                    onBack = { showExpense = false }
                ) { amount, category, note, date ->
                    transactionVM.addExpense(userId, amount, category, note, date)
                    showExpense = false
                }
            }
        }
    }

    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Xác nhận xóa", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa giao dịch này không? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionVM.deleteTransaction(userId, transactionToDelete!!.id)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun SummaryMiniCard(label: String, amount: Double, icon: ImageVector, contentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
            Text(text = formatCurrency(amount), color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionItem(
    transaction: com.example.qlct.model.Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    sdf: SimpleDateFormat
) {
    val isIncome = transaction.type == "income"
    val categoryColor = getCategoryColor(transaction.category, isIncome)
    val categoryIcon = getCategoryIcon(transaction.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                if (transaction.note.isNotEmpty()) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Text(
                    text = sdf.format(Date(transaction.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isIncome) "+" else "-"}${formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                )
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Xóa",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}