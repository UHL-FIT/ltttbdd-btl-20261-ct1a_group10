package com.example.qlct.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    transactionVM: com.example.qlct.viewmodel.TransactionViewModel,
    userId: String,
    isIncome: Boolean,
    initialAmount: Double = 0.0,
    initialCategory: String = "",
    initialNote: String = "",
    initialDate: Long = System.currentTimeMillis(),
    onBack: () -> Unit,
    onSave: (Double, String, String, Long) -> Unit
) {
    var amount by remember { mutableStateOf(if (initialAmount > 0) initialAmount.toLong().toString() else "") }
    var amountError by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf(initialNote) }
    var selectedDate by remember { mutableLongStateOf(initialDate) }
    
    val currentCategories = if (isIncome) transactionVM.incomeCategories else transactionVM.expenseCategories
    var selectedCategory by remember { 
        mutableStateOf(initialCategory.ifEmpty { currentCategories.firstOrNull() ?: "" })
    }
    
    var isEditMode by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (initialAmount > 0) "Chỉnh sửa" else "Thêm mới",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { isEditMode = !isEditMode }) {
                        Icon(
                            if (isEditMode) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = if (isEditMode) "Xong" else "Chỉnh sửa danh mục",
                            tint = if (isEditMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 16.dp
            ) {
                Button(
                    onClick = {
                        val money = amount.toDoubleOrNull() ?: 0.0
                        if (money <= 0) {
                            amountError = true
                        } else {
                            amountError = false
                            onSave(money, selectedCategory, note, selectedDate)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    )
                ) {
                    Text("Lưu giao dịch", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Amount Input Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = (if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F)).copy(alpha = 0.05f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isIncome) "Số tiền thu nhập" else "Số tiền chi tiêu",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                        TextField(
                            value = amount,
                            onValueChange = { 
                                if (it.all { char -> char.isDigit() }) {
                                    amount = it
                                    amountError = false
                                }
                            },
                            isError = amountError,
                            placeholder = { Text("0", color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                if (amountError) {
                                    Text("Vui lòng nhập số tiền hợp lệ", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                unfocusedTextColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                            ),
                            textStyle = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 36.sp
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            suffix = { Text("đ", style = MaterialTheme.typography.headlineSmall) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chọn hạng mục", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (isEditMode) {
                        Text("Đang chỉnh sửa", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Grid-like category selection
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 4
                ) {
                    currentCategories.forEach { categoryName ->
                        val isSelected = selectedCategory == categoryName
                        val categoryIcon = getCategoryIcon(categoryName)
                        val categoryColor = getCategoryColor(categoryName, isIncome)
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(70.dp)
                                .clickable { 
                                    if (!isEditMode) {
                                        selectedCategory = categoryName 
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) categoryColor else categoryColor.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else categoryColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                
                                if (isEditMode && currentCategories.size > 1) {
                                    IconButton(
                                        onClick = {
                                            transactionVM.removeCategory(userId, if(isIncome) "income" else "expense", categoryName)
                                            if (selectedCategory == categoryName) {
                                                selectedCategory = currentCategories.firstOrNull() ?: ""
                                            }
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 8.dp, y = (-8).dp)
                                            .size(20.dp)
                                            .background(MaterialTheme.colorScheme.error, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                    
                    // Add Category Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(70.dp)
                            .clickable { showAddCategoryDialog = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Thêm hạng mục",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Thêm",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
            }

            // Date Selection
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Ngày giao dịch", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = sdf.format(Date(selectedDate)).replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                }
            }

            // Note Input
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Thêm ghi chú...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom button
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis ?: selectedDate
                    showDatePicker = false
                }) {
                    Text("Xác nhận", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Thêm hạng mục mới") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Tên hạng mục") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        transactionVM.addCategory(userId, if(isIncome) "income" else "expense", newCategoryName)
                        selectedCategory = newCategoryName
                        newCategoryName = ""
                        showAddCategoryDialog = false
                    }
                }) {
                    Text("Thêm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

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
