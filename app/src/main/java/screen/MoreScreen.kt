package com.example.qlct.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.qlct.util.CurrencyFormat
import com.example.qlct.util.CurrencyPrefs
import com.example.qlct.viewmodel.TransactionViewModel
import com.google.gson.Gson
import android.widget.Toast
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.qlct.model.Transaction

@Composable
fun MoreScreen(transactionVM: TransactionViewModel, userId: String) {
    val context = LocalContext.current
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Launcher for Exporting JSON
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(transactionVM.transactions)
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
                Toast.makeText(context, "Xuất dữ liệu thành công!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher for Importing JSON
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    val gson = Gson()
                    val importedTransactions = gson.fromJson(jsonString, Array<Transaction>::class.java).toList()
                    
                    // Logic to import into Firestore via ViewModel
                    importedTransactions.forEach { trans ->
                        transactionVM.addTransaction(userId, trans.copy(id = "")) // Reset ID to let Firestore generate new
                    }
                    Toast.makeText(context, "Nhập ${importedTransactions.size} giao dịch thành công!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi nhập dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Cài đặt & Thêm",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Nhóm Cài đặt hệ thống
        item {
            Text(
                text = "Hệ thống",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }

        item {
            MoreOptionItem(
                title = "Định dạng tiền tệ",
                subtitle = CurrencyPrefs.currentFormat.displayName,
                icon = Icons.Default.Payments,
                onClick = { showCurrencyDialog = true }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Nhóm Dữ liệu
        item {
            Text(
                text = "Dữ liệu",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }

        item {
            MoreOptionItem(
                title = "Xuất dữ liệu JSON",
                subtitle = "Lưu tất cả giao dịch về máy",
                icon = Icons.Default.FileUpload,
                onClick = { exportLauncher.launch("QLCT_Data_${System.currentTimeMillis()}.json") }
            )
        }

        item {
            MoreOptionItem(
                title = "Nhập dữ liệu JSON",
                subtitle = "Khôi phục dữ liệu từ tệp",
                icon = Icons.Default.FileDownload,
                onClick = { importLauncher.launch(arrayOf("application/json")) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Nhóm Hỗ trợ & Thông tin
        item {
            Text(
                text = "Thông tin",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }

        item {
            MoreOptionItem(
                title = "Về ứng dụng",
                subtitle = "Phiên bản 1.1.0",
                icon = Icons.Default.Info,
                onClick = { showAboutDialog = true }
            )
        }
    }

    // --- DIALOGS ---

    // Chức năng Chọn Định dạng tiền tệ
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Chọn định dạng tiền tệ", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    CurrencyFormat.entries.forEach { format ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    CurrencyPrefs.saveFormat(context, format)
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (CurrencyPrefs.currentFormat == format),
                                onClick = {
                                    CurrencyPrefs.saveFormat(context, format)
                                    showCurrencyDialog = false
                                }
                            )
                            Text(
                                text = format.displayName,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    // Chức năng Thông tin ứng dụng
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Về ứng dụng QLCT", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Logo giả định
                    Surface(
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                    
                    Text("QLCT - Quản Lý Chi Tiêu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Text(
                        "Ứng dụng quản lý tài chính cá nhân đơn giản, tinh gọn và hiệu quả theo phong cách hiện đại.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        InfoRow(label = "Phiên bản", value = "1.1.0")
                        InfoRow(label = "Ngày cập nhật", value = "24/05/2024")
                        InfoRow(label = "Trạng thái", value = "Chính thức")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Tuyệt vời")
                }
            }
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MoreOptionItem(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
