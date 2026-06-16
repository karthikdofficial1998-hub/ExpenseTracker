package com.example.expensetracker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.viewmodel.ExpenseCategory
import com.example.expensetracker.viewmodel.categories

@Composable
fun AddExpenseScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String, Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var isExpense by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE0D7FF), Color.White)
                    )
                )
                .padding(top = 40.dp, start = 20.dp, end = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.padding(12.dp),
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "Add Expense",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.weight(1.2f))
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Toggle Switch
                Surface(
                    modifier = Modifier
                        .width(280.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F1F1))
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .background(
                                    if (isExpense) Color(0xFF8B66FF) else Color.Transparent,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { isExpense = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Expenses",
                                color = if (isExpense) Color.White else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .background(
                                    if (!isExpense) Color(0xFF8B66FF) else Color.Transparent,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { isExpense = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Income",
                                color = if (!isExpense) Color.White else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Amount Input
            Text(
                text = "₹${if (amount.isEmpty()) "0" else amount}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                "Enter Amount",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Categories
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    CategoryChipSmall(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Add Button
            Button(
                onClick = {
                    val amtValue = amount.toDoubleOrNull() ?: 0.0
                    if (amtValue > 0) {
                        onSaveClick(selectedCategory.name, amtValue, selectedCategory.name)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B66FF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Add Expense", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Custom Numeric Keypad
            NumericKeypad(
                onNumberClick = { num ->
                    if (amount.length < 10) {
                        if (num == "." && amount.contains(".")) return@NumericKeypad
                        amount += num
                    }
                },
                onDeleteClick = {
                    if (amount.isNotEmpty()) {
                        amount = amount.dropLast(1)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CategoryChipSmall(
    category: ExpenseCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF8B66FF) else Color(0xFFF1F1F1)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) Color(0xFF8B66FF) else Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color(0xFF8B66FF) else Color.Black
            )
        }
    }
}

@Composable
fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val keys = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        ".", "0", "DEL"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in 0 until 4) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until 3) {
                    val index = i * 3 + j
                    val key = keys[index]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clickable {
                                if (key == "DEL") onDeleteClick() else onNumberClick(key)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "DEL") {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete")
                        } else {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    ExpenseTrackerTheme {
        AddExpenseScreen(
            onBackClick = {},
            onSaveClick = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryChipSmallPreview() {
    ExpenseTrackerTheme {
        CategoryChipSmall(
            category = categories.first(),
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NumericKeypadPreview() {
    ExpenseTrackerTheme {
        NumericKeypad(
            onNumberClick = {},
            onDeleteClick = {}
        )
    }
}
