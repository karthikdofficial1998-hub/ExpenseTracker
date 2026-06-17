package com.example.expensetracker.ui.theme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.utils.NotificationHelper
import com.example.expensetracker.utils.PdfGenerator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    transactions: List<ExpenseEntity>,
    onBackClick: () -> Unit,
    onGaveClick: () -> Unit,
    onGotClick: () -> Unit
) {
    val totalGave = transactions.filter { it.isExpense }.sumOf { it.amount }
    val totalGot = transactions.filter { !it.isExpense }.sumOf { it.amount }
    val balance = totalGave - totalGot

    var previewImagePath by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.showReminderNotification(context, contact.name, Math.abs(balance))
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    NotificationHelper.createNotificationChannel(context)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            NotificationHelper.showReminderNotification(context, contact.name, Math.abs(balance))
                        } else {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        NotificationHelper.showReminderNotification(context, contact.name, Math.abs(balance))
                    }
                }) {
                    Text("SET")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val sortedTransactions = remember(transactions) {
        transactions.sortedBy { it.date }
    }

    val transactionsWithRunningBalance = remember(sortedTransactions) {
        var currentBal = 0.0
        sortedTransactions.map { transaction ->
            if (transaction.isExpense) {
                currentBal += transaction.amount // You gave = they owe you more
            } else {
                currentBal -= transaction.amount // You got = they owe you less
            }
            transaction to currentBal
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(contact.initial, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(contact.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("View settings", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF5E49BF))
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onGaveClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("YOU GAVE ₹", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onGotClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("YOU GOT ₹", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F7FB))
        ) {
            // Balance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (balance >= 0) "You will get" else "You will give",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "₹${Math.abs(balance).toInt()}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (balance >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8EAFE), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .clickable { showDatePicker = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF5E49BF), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Set collection reminder", color = Color(0xFF5E49BF), modifier = Modifier.weight(1f))
                        Text("SET DATE", color = Color(0xFF5E49BF), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Quick Actions
            val context = LocalContext.current
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionItem(
                    icon = Icons.Default.PictureAsPdf,
                    label = "Report",
                    onClick = {
                        val pdfFile = PdfGenerator.generateContactReport(context, contact, transactions)
                        if (pdfFile != null) {
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                pdfFile
                            )
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Failed to generate report", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                QuickActionItem(icon = Icons.Default.Share, label = "Reminder", onClick = {})
                QuickActionItem(icon = Icons.Default.Sms, label = "SMS", onClick = {})
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
                        Text("Only you and ${contact.name} can see these entries", color = Color.Gray, modifier = Modifier.padding(16.dp))
                        Spacer(modifier = Modifier.height(100.dp))
                        Text("Start adding transactions with ${contact.name}", color = Color.Gray)
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFF5E49BF), modifier = Modifier.size(32.dp))
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ENTRIES", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Row {
                        Text("YOU GAVE", fontSize = 12.sp, color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(40.dp))
                        Text("YOU GOT", fontSize = 12.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                    items(transactionsWithRunningBalance.reversed()) { (transaction, runningBalance) ->
                        TransactionEntryItem(transaction, runningBalance) { path ->
                            previewImagePath = path
                        }
                    }
                }
            }
        }

        // Full Screen Preview
        if (previewImagePath != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { previewImagePath = null },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(File(previewImagePath!!)),
                    contentDescription = "Full Screen Bill",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { previewImagePath = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF5E49BF))
        Text(label, fontSize = 12.sp, color = Color(0xFF5E49BF))
    }
}

@Composable
fun TransactionEntryItem(transaction: ExpenseEntity, currentBalance: Double, onImageClick: (String) -> Unit) {
    val timestamp = remember(transaction.date) {
        val sdf = SimpleDateFormat("dd MMM yy • hh:mm a", Locale.getDefault())
        sdf.format(Date(transaction.date))
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                Text(timestamp, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (currentBalance >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Bal. ₹${Math.abs(currentBalance).toInt()}",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = if (currentBalance >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(transaction.title, fontWeight = FontWeight.Medium)
                
                if (transaction.imagePath != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(File(transaction.imagePath)),
                        contentDescription = "Attached Bill",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF1F1F1))
                            .clickable { onImageClick(transaction.imagePath!!) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            // Vertical Divider
            Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color(0xFFEEEEEE)))

            // Gave Column
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .background(if (transaction.isExpense) Color(0xFFFFEBEE) else Color.Transparent)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (transaction.isExpense) {
                    Text("₹ ${transaction.amount.toInt()}", color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)
                }
            }

            // Vertical Divider
            Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color(0xFFEEEEEE)))

            // Got Column
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .background(if (!transaction.isExpense) Color(0xFFE8F5E9) else Color.Transparent)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!transaction.isExpense) {
                    Text("₹ ${transaction.amount.toInt()}", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
