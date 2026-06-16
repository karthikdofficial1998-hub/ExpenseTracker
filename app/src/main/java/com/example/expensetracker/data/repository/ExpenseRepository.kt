package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.local.ExpenseEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val dao: ExpenseDao
) {

    suspend fun getExpenses(): Flow<List<ExpenseEntity>> {
        delay(3000)
        return dao.getAllExpenses()
    }

    suspend fun addExpense(expense: ExpenseEntity){
        delay(300)
        dao.insertExpense(expense)
    }

    suspend fun deleteExpense(
        expense: ExpenseEntity
    ) {

        delay(500)

       dao.deleteExpense(expense)
    }

}