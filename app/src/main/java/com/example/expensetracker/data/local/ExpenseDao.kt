package com.example.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {


    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)
    @Insert
    suspend fun insertExpense(
        expense: ExpenseEntity
    )

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>  // List<ExpenseEntity> this also can use but run only once
// if i want updated data need run again  But using this flow once database changes emits data automatically


}