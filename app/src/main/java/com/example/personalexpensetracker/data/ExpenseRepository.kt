package com.example.personalexpensetracker.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Simple local persistence: the full expense list is stored as a JSON array
 * in SharedPreferences. Plenty for a lightweight personal tracker.
 */
class ExpenseRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadExpenses(): List<Expense> {
        val raw = prefs.getString(KEY_EXPENSES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                Expense(
                    id = item.getLong(KEY_ID),
                    amount = item.getDouble(KEY_AMOUNT),
                    category = item.getString(KEY_CATEGORY),
                    note = item.optString(KEY_NOTE),
                    timestamp = item.getLong(KEY_TIMESTAMP),
                )
            }
        }.getOrDefault(emptyList())
    }

    fun saveExpenses(expenses: List<Expense>) {
        val array = JSONArray()
        expenses.forEach { expense ->
            array.put(
                JSONObject()
                    .put(KEY_ID, expense.id)
                    .put(KEY_AMOUNT, expense.amount)
                    .put(KEY_CATEGORY, expense.category)
                    .put(KEY_NOTE, expense.note)
                    .put(KEY_TIMESTAMP, expense.timestamp),
            )
        }
        prefs.edit().putString(KEY_EXPENSES, array.toString()).apply()
    }

    private companion object {
        const val PREFS_NAME = "expense_tracker"
        const val KEY_EXPENSES = "expenses"
        const val KEY_ID = "id"
        const val KEY_AMOUNT = "amount"
        const val KEY_CATEGORY = "category"
        const val KEY_NOTE = "note"
        const val KEY_TIMESTAMP = "timestamp"
    }
}
