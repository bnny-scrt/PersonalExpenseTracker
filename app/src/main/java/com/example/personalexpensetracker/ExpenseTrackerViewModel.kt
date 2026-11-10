package com.example.personalexpensetracker

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.personalexpensetracker.data.Expense
import com.example.personalexpensetracker.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Holds all expenses and persists every change immediately. */
class ExpenseTrackerViewModel(context: Context) : ViewModel() {

    private val repository = ExpenseRepository(context)

    private val _expenses = MutableStateFlow(repository.loadExpenses())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    fun addExpense(amount: Double, category: String, note: String, timestamp: Long) {
        val expense = Expense(
            id = generateId(),
            amount = amount,
            category = category,
            note = note,
            timestamp = timestamp,
        )
        _expenses.value = (_expenses.value + expense).sortedByDescending { it.timestamp }
        repository.saveExpenses(_expenses.value)
    }

    fun deleteExpense(expense: Expense) {
        _expenses.value = _expenses.value.filterNot { it.id == expense.id }
        repository.saveExpenses(_expenses.value)
    }

    private fun generateId(): Long {
        var id = System.currentTimeMillis()
        while (_expenses.value.any { it.id == id }) id += 1
        return id
    }
}
