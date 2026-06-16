package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.expensetracker.data.local.ExpenseDatabase
import com.example.expensetracker.data.local.ExpenseViewModelFactory
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.theme.AddExpenseScreen
import com.example.expensetracker.ui.theme.ExpenseScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            ExpenseDatabase::class.java,
            "expense_db"
        ).build()

        val repository =
            ExpenseRepository(
                db.expenseDao()
            )

        val factory =
            ExpenseViewModelFactory(
                repository
            )

        setContent {
            ExpenseTrackerTheme {
                val vm: ExpenseViewModel =
                    viewModel(
                        factory = factory
                    )
                
                var currentScreen by remember { mutableStateOf("list") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentScreen == "list") {
                        ExpenseScreen(
                            viewModel = vm,
                            onAddExpenseClick = { currentScreen = "add_expense" }
                        )
                    } else {
                        AddExpenseScreen(
                            onBackClick = { currentScreen = "list" },
                            onSaveClick = { title, amount, category ->
                                vm.addExpense(title, amount, category)
                                currentScreen = "list"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExpenseTrackerTheme {
        Greeting("Android")
    }
}