package com.example.personalexpensetracker.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** A single recorded expense. */
data class Expense(
    val id: Long,
    val amount: Double,
    val category: String,
    val note: String,
    val timestamp: Long,
)

/** Fixed set of spending categories, each mapped to a Material icon in the UI. */
object ExpenseCategory {
    val all = listOf(
        "Food",
        "Transport",
        "Shopping",
        "Entertainment",
        "Bills",
        "Health",
        "Other",
    )
}

/** Date/time helpers for grouping expenses by month and day. All single-threaded on the UI thread. */
object ExpenseDateUtils {

    /** Philippines timezone — the app records and displays all times as Philippine time. */
    private val MANILA = TimeZone.getTimeZone("Asia/Manila")

    private val monthKeyFormat = SimpleDateFormat("yyyy-MM", Locale.US).apply {
        timeZone = MANILA
    }
    private val dayKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = MANILA
    }
    private val monthLabelFormat = SimpleDateFormat("MMMM yyyy", Locale.US).apply {
        timeZone = MANILA
    }
    private val dayLabelFormat = SimpleDateFormat("EEE, MMM d", Locale.US).apply {
        timeZone = MANILA
    }
    private val datePickerFormat = SimpleDateFormat("MMM d, yyyy", Locale.US).apply {
        timeZone = MANILA
    }
    private val timeLabelFormat = SimpleDateFormat("h:mm a", Locale.US).apply {
        timeZone = MANILA
    }

    fun monthKey(timestamp: Long): String = monthKeyFormat.format(Date(timestamp))

    fun monthKey(calendar: Calendar): String = monthKeyFormat.format(calendar.time)

    fun monthLabel(calendar: Calendar): String = monthLabelFormat.format(calendar.time)

    fun dayKey(timestamp: Long): String = dayKeyFormat.format(Date(timestamp))

    /** e.g. "Today", "Yesterday" or "Wed, Nov 4" */
    fun dayLabel(timestamp: Long): String {
        val today = dayKey(System.currentTimeMillis())
        return when (dayKey(timestamp)) {
            today -> "Today"
            dayKey(System.currentTimeMillis() - 24L * 60 * 60 * 1000) -> "Yesterday"
            else -> dayLabelFormat.format(Date(timestamp))
        }
    }

    /** Exact Philippine time of recording, e.g. "3:45 PM". */
    fun timeLabel(timestamp: Long): String = timeLabelFormat.format(Date(timestamp))

    /** Short label used on the date picker button, e.g. "Today, Nov 5" / "Nov 3, 2026". */
    fun shortDateLabel(timestamp: Long): String = datePickerFormat.format(Date(timestamp))

    /**
     * Converts a UTC-midnight millis (as returned by Material3 date picker) to a local
     * timestamp at 12:00 noon, which is safe across DST changes.
     */
    fun localNoonFromUtcMidnight(utcMillis: Long): Long {
        val utc = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        utc.timeInMillis = utcMillis
        val local = Calendar.getInstance()
        local.set(
            utc.get(Calendar.YEAR),
            utc.get(Calendar.MONTH),
            utc.get(Calendar.DAY_OF_MONTH),
            12,
            0,
            0,
        )
        local.set(Calendar.MILLISECOND, 0)
        return local.timeInMillis
    }
}
