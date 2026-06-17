package com.example.expensetracker.viewmodel

import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.data.local.ExpenseEntity

data class ExpenseUiState(
    val isLoading: Boolean = true,
    val expenses: List<ExpenseEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val userName: String = "John Doe",
    val userMobile: String = "9876543210",
    val userPhotoUri: String? = null
)
