package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
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
import com.example.expensetracker.data.local.UserPreferences
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.theme.AddExpenseScreen
import com.example.expensetracker.ui.theme.AnalyticsScreen
import com.example.expensetracker.ui.theme.BudgetScreen
import com.example.expensetracker.ui.theme.Contact
import com.example.expensetracker.ui.theme.ContactDetailScreen
import com.example.expensetracker.ui.theme.ContactScreen
import com.example.expensetracker.ui.theme.ExpenseScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.theme.NewTransactionScreen
import com.example.expensetracker.ui.theme.ProfileScreen
import com.example.expensetracker.ui.theme.SplashScreen
import com.example.expensetracker.viewmodel.ExpenseViewModel
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            ExpenseDatabase::class.java,
            "expense_db"
        ).fallbackToDestructiveMigration().build()

        val userPreferences = UserPreferences(applicationContext)

        val repository =
            ExpenseRepository(
                db.expenseDao(),
                db.budgetDao(),
                userPreferences
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
                
                var currentScreen by remember { mutableStateOf("splash") }
                var selectedContact by remember { mutableStateOf<Contact?>(null) }
                var isExpenseForNewTransaction by remember { mutableStateOf(true) }

                val uiState by vm.uiState.collectAsState()
                val allExpenses = uiState.expenses

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BackHandler(enabled = currentScreen != "splash" && currentScreen != "list") {
                        currentScreen = when (currentScreen) {
                            "contact_detail" -> "contacts"
                            "new_transaction" -> "contact_detail"
                            else -> "list"
                        }
                    }
                    when (currentScreen) {
                        "splash" -> {
                            SplashScreen(onTimeout = { currentScreen = "list" })
                        }
                        "list" -> {
                            ExpenseScreen(
                                viewModel = vm,
                                onAddExpenseClick = { currentScreen = "add_expense" },
                                onContactClick = { currentScreen = "contacts" },
                                onAnalyticsClick = { currentScreen = "analytics" },
                                onBudgetClick = { currentScreen = "budget" },
                                onProfileClick = { currentScreen = "profile" }
                            )
                        }
                        "profile" -> {
                            ProfileScreen(
                                viewModel = vm,
                                onBackClick = { currentScreen = "list" }
                            )
                        }
                        "analytics" -> {
                            AnalyticsScreen(
                                expenses = allExpenses,
                                onBackClick = { currentScreen = "list" }
                            )
                        }
                        "budget" -> {
                            BudgetScreen(
                                viewModel = vm,
                                onBackClick = { currentScreen = "list" }
                            )
                        }
                        "add_expense" -> {
                            AddExpenseScreen(
                                onBackClick = { currentScreen = "list" },
                                onSaveClick = { title, amount, category, isExpense ->
                                    vm.addExpense(title, amount, category, isExpense)
                                    currentScreen = "list"
                                }
                            )
                        }
                        "contacts" -> {
                            ContactScreen(
                                onBackClick = { currentScreen = "list" },
                                onContactClick = { contact ->
                                    selectedContact = contact
                                    currentScreen = "contact_detail"
                                }
                            )
                        }
                        "contact_detail" -> {
                            selectedContact?.let { contact ->
                                val contactTransactions = allExpenses.filter { it.contactName == contact.name }
                                ContactDetailScreen(
                                    contact = contact,
                                    transactions = contactTransactions,
                                    onBackClick = { currentScreen = "contacts" },
                                    onGaveClick = {
                                        isExpenseForNewTransaction = true
                                        currentScreen = "new_transaction"
                                    },
                                    onGotClick = {
                                        isExpenseForNewTransaction = false
                                        currentScreen = "new_transaction"
                                    }
                                )
                            }
                        }
                        "new_transaction" -> {
                            selectedContact?.let { contact ->
                                NewTransactionScreen(
                                    contact = contact,
                                    isExpenseInitial = isExpenseForNewTransaction,
                                    onBackClick = { currentScreen = "contact_detail" },
                                    onSaveClick = { title, amount, category, isExpense, date, imagePath ->
                                        vm.addExpense(title, amount, category, isExpense, contact.name, date, imagePath)
                                        currentScreen = "contact_detail"
                                    }
                                )
                            }
                        }
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