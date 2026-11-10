package com.example.personalexpensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.Composable
import com.example.personalexpensetracker.ui.ExpenseTrackerApp
import com.example.personalexpensetracker.ui.theme.PersonalExpenseTrackerTheme

class MainActivity : ComponentActivity() {

    private val expenseTrackerFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val application = checkNotNull(
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY],
            )
            ExpenseTrackerViewModel(application)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalExpenseTrackerTheme {
                ExpenseTrackerScreen()
            }
        }
    }

    @Composable
    private fun ExpenseTrackerScreen() {
        val viewModel: ExpenseTrackerViewModel = viewModel(factory = expenseTrackerFactory)
        ExpenseTrackerApp(viewModel = viewModel)
    }
}

