package com.example.expensetracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.getExpenses(),
                repository.getAllBudgets(),
                repository.userName,
                repository.userMobile,
                repository.userPhotoUri
            ) { expenses, budgets, name, mobile, photo ->
                val totalExpense = expenses.filter { it.isExpense }.sumOf { it.amount }
                val totalIncome = expenses.filter { !it.isExpense }.sumOf { it.amount }

                _uiState.value.copy(
                    isLoading = false,
                    expenses = expenses,
                    budgets = budgets,
                    totalExpense = totalExpense,
                    totalIncome = totalIncome,
                    userName = name,
                    userMobile = mobile,
                    userPhotoUri = photo
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun saveProfile(name: String, mobile: String, photoUri: String?) {
        viewModelScope.launch {
            repository.saveProfile(name, mobile, photoUri)
        }
    }

    fun setBudget(category: String, limit: Double) {
        viewModelScope.launch {
            repository.insertBudget(BudgetEntity(category, limit))
        }
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        isExpense: Boolean,
        contactName: String? = null,
        date: Long = System.currentTimeMillis(),
        imagePath: String? = null
    ) {

        viewModelScope.launch {

            repository.addExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    isExpense = isExpense,
                    contactName = contactName,
                    date = date,
                    imagePath = imagePath
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