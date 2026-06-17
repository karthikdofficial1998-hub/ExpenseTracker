package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.BudgetDao
import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.local.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val dao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val userPreferences: UserPreferences
) {

    fun getExpenses(): Flow<List<ExpenseEntity>> {
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

    fun getAllBudgets(): Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }

    val userName: Flow<String> = userPreferences.userName
    val userMobile: Flow<String> = userPreferences.userMobile
    val userPhotoUri: Flow<String?> = userPreferences.userPhotoUri

    suspend fun saveProfile(name: String, mobile: String, photoUri: String?) {
        userPreferences.saveProfile(name, mobile, photoUri)
    }
}