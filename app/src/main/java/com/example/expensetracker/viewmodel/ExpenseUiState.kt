package com.example.expensetracker.viewmodel

import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository

data class ExpenseUiState(
    val isLoading: Boolean = true,

    val expenses: List<ExpenseEntity> =
        emptyList(),

    val totalExpense: Double = 0.0
)
