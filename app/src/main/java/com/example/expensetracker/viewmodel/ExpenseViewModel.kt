package com.example.expensetracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ExpenseUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    init {
        observeExpenses()
    }

    private fun observeExpenses() {

        viewModelScope.launch {

            repository
                .getExpenses()
                .collect { expenses ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            expenses = expenses,
                            totalExpense =
                                expenses.sumOf {
                                    it.amount
                                }
                        )
                }
        }
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: String
    ) {

        viewModelScope.launch {

            repository.addExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category
                )
            )
        }
    }

    fun deleteExpense(
        expense: ExpenseEntity
    ) {

        viewModelScope.launch {

            repository.deleteExpense(
                expense
            )
        }
    }
}