package com.example.expensetracker.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ExpenseCategory(
    val name:  String,
    val icon: ImageVector,
    val color: Color
)

val categories = listOf(
    ExpenseCategory("Food", Icons.Default.Fastfood, Color(0xFFFDE8E8)),
    ExpenseCategory("Travel", Icons.Default.Flight, Color(0xFFE8F0FE)),
    ExpenseCategory("Shopping", Icons.Default.ShoppingCart, Color(0xFFFEF2E8)),
    ExpenseCategory("Bills", Icons.Default.Receipt, Color(0xFFFEF9E8)),
    ExpenseCategory("Entertainment", Icons.Default.Movie, Color(0xFFF3E8FE)),
    ExpenseCategory("Contacts", Icons.Default.Person, Color(0xFFE8EAFE)),
    ExpenseCategory("Others", Icons.Default.Category, Color(0xFFE8FEF3))
)
