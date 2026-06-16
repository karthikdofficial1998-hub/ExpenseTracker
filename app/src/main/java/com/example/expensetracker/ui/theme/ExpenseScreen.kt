package com.example.expensetracker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.viewmodel.ExpenseCategory
import com.example.expensetracker.viewmodel.ExpenseUiState
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.example.expensetracker.viewmodel.categories
import kotlinx.coroutines.launch

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    onAddExpenseClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    ExpenseScreenContent(
        uiState = uiState,
        onAddExpenseClick = onAddExpenseClick,
        onDeleteExpense = { viewModel.deleteExpense(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreenContent(
    uiState: ExpenseUiState,
    onAddExpenseClick: () -> Unit = {},
    onDeleteExpense: (ExpenseEntity) -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(categories.first()) }
    var selectedDrawerItem by remember { mutableStateOf("Dashboard") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF5E49BF), Color(0xFF8066E0))
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "John Doe",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "john.doe@example.com",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Menu Items
                val menuItems = listOf(
                    "Dashboard" to Icons.Default.Dashboard,
                    "Transactions" to Icons.AutoMirrored.Filled.ReceiptLong,
                    "Analytics" to Icons.Default.PieChart,
                    "Budget" to Icons.Default.Wallet,
                    "Categories" to Icons.Default.Category,
                    "Settings" to Icons.Default.Settings,
                    "Help" to Icons.AutoMirrored.Filled.HelpOutline
                )

                menuItems.forEach { (label, icon) ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                label,
                                fontWeight = if (selectedDrawerItem == label) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selectedDrawerItem == label,
                        onClick = {
                            selectedDrawerItem = label
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(icon, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFF3E8FE),
                            selectedIconColor = Color(0xFF5E49BF),
                            selectedTextColor = Color(0xFF5E49BF),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))

                NavigationDrawerItem(
                    label = { Text("Logout", color = Color.Red) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF5E49BF),
                            selectedTextColor = Color(0xFF5E49BF),
                            indicatorColor = Color(0xFFF3E8FE)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Default.PieChart, contentDescription = null) },
                        label = { Text("Analytics") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Default.Wallet, contentDescription = null) },
                        label = { Text("Budget") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile") }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddExpenseClick,
                    containerColor = Color(0xFF5E49BF),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.offset(y = 40.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .background(Color(0xFFF8F9FE))
            ) {
                // Header Section
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF5E49BF), Color(0xFF8066E0))
                                ),
                                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Open Sidebar",
                                    tint = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Expense Tracker",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "June 2025",
                                        color = Color.White.copy(alpha = 0.8f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Row {
                                IconButton(onClick = { }) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                IconButton(onClick = { }) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                // Balance Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .offset(y = (-80).dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Total Balance",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "₹12,450",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color(0xFFF3E8FE), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Wallet,
                                        contentDescription = null,
                                        tint = Color(0xFF5E49BF),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = Color(0xFFF1F1F1))
                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Income
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color(0xFFE8F5E9), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Income",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                        Text(
                                            "₹15,000",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Expense
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color(0xFFFFEBEE), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = Color(0xFFF44336),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Expense",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                        Text(
                                            "₹2,550",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color(0xFFF44336),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }


                // Search Bar
                item {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .offset(y = (-60).dp),
                        placeholder = { Text("Search expenses...", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                // Categories Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .offset(y = (-40).dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Categories",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "View All",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5E49BF)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(categories) { category ->
                                CategoryChip(
                                    category = category,
                                    isSelected = selectedCategory == category,
                                    onClick = { selectedCategory = category }
                                )
                            }
                        }
                    }
                }

                // Recent Expenses
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .offset(y = (-20).dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Expenses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "See All",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E49BF)
                        )
                    }
                }

                items(uiState.expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        onDelete = { onDeleteExpense(expense) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .offset(y = (-20).dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: ExpenseCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(category.color, RoundedCornerShape(16.dp))
                .then(
                    if (isSelected) Modifier.border(
                        2.dp,
                        Color(0xFF5E49BF),
                        RoundedCornerShape(16.dp)
                    )
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = Color(0xFF5E49BF),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.name, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ExpenseItem(
    expense: ExpenseEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = categories.find { it.name == expense.category } ?: categories.last()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(category.color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = Color(0xFF5E49BF),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Today, 1:30 PM", // Hardcoded for UI match
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${expense.amount.toInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5E49BF)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Expense", tint = Color.Gray)
            }
        }
    }
}
