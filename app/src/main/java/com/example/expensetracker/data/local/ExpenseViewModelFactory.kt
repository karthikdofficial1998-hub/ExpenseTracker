package com.example.expensetracker.data.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.viewmodel.ExpenseViewModel

class ExpenseViewModelFactory(private val repository: ExpenseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                ExpenseViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }

}