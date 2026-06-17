package com.example.expensetracker.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.categories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    expenses: List<ExpenseEntity>,
    onBackClick: () -> Unit
) {
    val totalExpense = expenses.filter { it.isExpense }.sumOf { it.amount }
    val categoryTotals = remember(expenses) {
        expenses.filter { it.isExpense }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spending Analytics", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FE))
        ) {
            item {
                SpendingOverviewCard(totalExpense, categoryTotals)
            }

            item {
                Text(
                    "Category Breakdown",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(categoryTotals) { (categoryName, amount) ->
                val category = categories.find { it.name == categoryName } ?: categories.last()
                CategoryProgressItem(
                    name = categoryName,
                    amount = amount,
                    total = totalExpense,
                    color = getVibrantColor(categoryName),
                    icon = category.icon
                )
            }
        }
    }
}

private fun getVibrantColor(categoryName: String): Color {
    return when (categoryName) {
        "Food" -> Color(0xFFFF5252)
        "Travel" -> Color(0xFF448AFF)
        "Shopping" -> Color(0xFFFF9800)
        "Bills" -> Color(0xFFFFC107)
        "Entertainment" -> Color(0xFF7C4DFF)
        "Contacts" -> Color(0xFF00BFA5)
        else -> Color(0xFF9E9E9E)
    }
}

@Composable
fun SpendingOverviewCard(totalExpense: Double, categoryTotals: List<Pair<String, Double>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.background(
            Brush.linearGradient(
                colors = listOf(Color.White, Color(0xFFF8F9FE))
            )
        )) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Total Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Text(
                    "₹${totalExpense.toInt()}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D2D2D)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Box(modifier = Modifier.size(220.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(200.dp)) {
                        var startAngle = -90f
                        if (totalExpense > 0) {
                            categoryTotals.forEach { (name, amount) ->
                                val sweepAngle = (amount.toFloat() / totalExpense.toFloat()) * 360f
                                drawArc(
                                    color = getVibrantColor(name),
                                    startAngle = startAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    style = Stroke(width = 45f, cap = StrokeCap.Round)
                                )
                                startAngle += sweepAngle
                            }
                        } else {
                            drawCircle(
                                color = Color.LightGray.copy(alpha = 0.3f),
                                style = Stroke(width = 45f)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Main Focus", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        Text(
                            categoryTotals.firstOrNull()?.first ?: "None",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5E49BF)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryProgressItem(name: String, amount: Double, total: Double, color: Color, icon: ImageVector) {
    val percentage = if (total > 0) (amount / total).toFloat() else 0f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹${amount.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { percentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${(percentage * 100).toInt()}% of total expenses",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    val sampleExpenses = listOf(
        ExpenseEntity(id = 1, title = "Lunch", amount = 200.0, category = "Food", isExpense = true),
        ExpenseEntity(id = 2, title = "Flight", amount = 5000.0, category = "Travel", isExpense = true),
        ExpenseEntity(id = 3, title = "Shopping", amount = 1500.0, category = "Shopping", isExpense = true),
        ExpenseEntity(id = 4, title = "Salary", amount = 50000.0, category = "Others", isExpense = false)
    )
    ExpenseTrackerTheme {
        AnalyticsScreen(
            expenses = sampleExpenses,
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpendingOverviewCardPreview() {
    val totalExpense = 6700.0
    val categoryTotals = listOf(
        "Travel" to 5000.0,
        "Shopping" to 1500.0,
        "Food" to 200.0
    )
    ExpenseTrackerTheme {
        SpendingOverviewCard(totalExpense = totalExpense, categoryTotals = categoryTotals)
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryProgressItemPreview() {
    ExpenseTrackerTheme {
        CategoryProgressItem(
            name = "Food",
            amount = 200.0,
            total = 6700.0,
            color = Color(0xFFFF5252),
            icon = Icons.Default.Fastfood
        )
    }
}
