package com.example.personalexpensetracker.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalexpensetracker.ExpenseTrackerViewModel
import com.example.personalexpensetracker.data.Expense
import com.example.personalexpensetracker.data.ExpenseCategory
import com.example.personalexpensetracker.data.ExpenseDateUtils
import com.example.personalexpensetracker.ui.theme.CatBlue
import com.example.personalexpensetracker.ui.theme.CatEmerald
import com.example.personalexpensetracker.ui.theme.CatOrange
import com.example.personalexpensetracker.ui.theme.CatRose
import com.example.personalexpensetracker.ui.theme.CatSky
import com.example.personalexpensetracker.ui.theme.CatSlate
import com.example.personalexpensetracker.ui.theme.CatViolet
import com.example.personalexpensetracker.ui.theme.SteelBackground
import com.example.personalexpensetracker.ui.theme.SteelBorder
import com.example.personalexpensetracker.ui.theme.SteelCard
import com.example.personalexpensetracker.ui.theme.SteelDeepBlue
import com.example.personalexpensetracker.ui.theme.SteelForeground
import com.example.personalexpensetracker.ui.theme.SteelMuted
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

private val moneyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("fil", "PH"))

fun formatMoney(amount: Double): String = moneyFormat.format(amount)

/** Distinct accent color per spending category. */
private fun categoryAccent(category: String): Color = when (category) {
    "Food" -> CatOrange
    "Transport" -> CatBlue
    "Shopping" -> CatViolet
    "Entertainment" -> CatRose
    "Bills" -> CatSky
    "Health" -> CatEmerald
    else -> CatSlate
}

/** Real Material vector icon per spending category. */
private fun categoryIcon(category: String): ImageVector = when (category) {
    "Food" -> Icons.Filled.Restaurant
    "Transport" -> Icons.Filled.DirectionsBus
    "Shopping" -> Icons.Filled.ShoppingBag
    "Entertainment" -> Icons.Filled.Movie
    "Bills" -> Icons.Filled.Receipt
    "Health" -> Icons.Filled.HealthAndSafety
    else -> Icons.Filled.Category
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerApp(viewModel: ExpenseTrackerViewModel) {
    val expenses by viewModel.expenses.collectAsState()
    var monthOffset by remember { mutableStateOf(0) }
    var showAddSheet by remember { mutableStateOf(false) }

    val monthCalendar = remember(monthOffset) {
        Calendar.getInstance().apply { add(Calendar.MONTH, monthOffset) }
    }
    val monthKey = ExpenseDateUtils.monthKey(monthCalendar)
    val monthExpenses = remember(expenses, monthKey) {
        expenses.filter { ExpenseDateUtils.monthKey(it.timestamp) == monthKey }
    }
    val monthTotal = monthExpenses.sumOf { it.amount }
    val topCategory = remember(monthExpenses) {
        monthExpenses
            .groupBy { it.category }
            .map { (category, list) -> category to list.sumOf { it.amount } }
            .maxByOrNull { it.second }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add expense")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            MonthlySummaryCard(
                monthLabel = ExpenseDateUtils.monthLabel(monthCalendar),
                monthTotal = monthTotal,
                expenseCount = monthExpenses.size,
                topCategory = topCategory,
                monthOffset = monthOffset,
                onPreviousMonth = { monthOffset -= 1 },
                onNextMonth = { monthOffset += 1 },
            )

            if (monthExpenses.isEmpty()) {
                EmptyMonthState()
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel("SPENDING BY CATEGORY")
                Spacer(modifier = Modifier.height(12.dp))
                CategoryBreakdown(monthExpenses)
                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel("ALL EXPENSES")
                Spacer(modifier = Modifier.height(4.dp))
                ExpenseListByDay(monthExpenses, viewModel::deleteExpense)
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    if (showAddSheet) {
        AddExpenseSheet(
            onDismiss = { showAddSheet = false },
            onSave = { amount, category, note, timestamp ->
                viewModel.addExpense(amount, category, note, timestamp)
                showAddSheet = false
            },
        )
    }
}

/** Engraved-style uppercase section label. */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.8.sp,
        color = SteelMuted,
    )
}

/** Gradient hero card: month navigation, big total, mini stat pills. */
@Composable
private fun MonthlySummaryCard(
    monthLabel: String,
    monthTotal: Double,
    expenseCount: Int,
    topCategory: Pair<String, Double>?,
    monthOffset: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    val daysElapsed = if (monthOffset == 0) {
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    } else {
        Calendar.getInstance()
            .apply { add(Calendar.MONTH, monthOffset) }
            .getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    val avgPerDay = monthTotal / daysElapsed.coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(SteelDeepBlue, SteelBackground),
                ),
            )
            .border(1.dp, SteelBorder, RoundedCornerShape(24.dp))
            .padding(20.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous month",
                        tint = SteelMuted,
                    )
                }
                Text(
                    text = monthLabel.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = SteelForeground,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next month",
                        tint = SteelMuted,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "TOTAL SPENT",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp,
                color = SteelMuted,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatMoney(monthTotal),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill(
                    label = if (expenseCount == 1) "ENTRY" else "ENTRIES",
                    value = expenseCount.toString(),
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.primary,
                )
                StatPill(
                    label = "PER DAY",
                    value = formatMoney(avgPerDay),
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.secondary,
                )
                topCategory?.let { (category, total) ->
                    StatPill(
                        label = "TOP · ${category.uppercase()}",
                        value = formatMoney(total),
                        modifier = Modifier.weight(1f),
                        accent = categoryAccent(category),
                    )
                }
            }
        }
    }
}

/** Small glass-like stat chip inside the hero card. */
@Composable
private fun StatPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SteelForeground.copy(alpha = 0.05f))
            .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
                color = SteelMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CategoryBreakdown(monthExpenses: List<Expense>) {
    val totalsByCategory = remember(monthExpenses) {
        monthExpenses
            .groupBy { it.category }
            .map { (category, list) -> category to list.sumOf { it.amount } }
            .sortedByDescending { it.second }
    }
    val max = totalsByCategory.maxOf { it.second }

    Column {
        totalsByCategory.forEach { (category, total) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryBadge(category)
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = formatMoney(total),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (total / max).toFloat().coerceIn(0f, 1f) },
                        color = categoryAccent(category),
                        trackColor = categoryAccent(category).copy(alpha = 0.15f),
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                    )
                }
            }
        }
    }
}

/** Colored circular category badge with a real Material icon. */
@Composable
private fun CategoryBadge(category: String, size: Int = 44) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(categoryAccent(category).copy(alpha = 0.18f))
            .border(1.dp, categoryAccent(category).copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = categoryIcon(category),
            contentDescription = category,
            tint = categoryAccent(category),
            modifier = Modifier.size((size * 0.5f).dp),
        )
    }
}

@Composable
private fun ExpenseListByDay(monthExpenses: List<Expense>, onDelete: (Expense) -> Unit) {
    val grouped = remember(monthExpenses) {
        monthExpenses.groupBy { ExpenseDateUtils.dayKey(it.timestamp) }
    }
    Column {
        grouped.forEach { (_, dayExpenses) ->
            val label = ExpenseDateUtils.dayLabel(dayExpenses.first().timestamp)
            val dayTotal = dayExpenses.sumOf { it.amount }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = formatMoney(dayTotal),
                    style = MaterialTheme.typography.labelLarge,
                    color = SteelMuted,
                )
            }
            dayExpenses.forEach { expense ->
                ExpenseRow(expense, onDelete)
            }
            HorizontalDivider(color = SteelMuted.copy(alpha = 0.1f))
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense, onDelete: (Expense) -> Unit) {
    // Two-step delete: step 1 arms the row, step 2 (within the timeout) actually deletes.
    var confirmDelete by remember(expense.id) { mutableStateOf(false) }

    LaunchedEffect(confirmDelete) {
        if (confirmDelete) {
            delay(4000)
            confirmDelete = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (confirmDelete) {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                } else {
                    Color.Transparent
                },
            )
            .padding(horizontal = if (confirmDelete) 12.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryBadge(expense.category)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.category,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (expense.note.isNotBlank()) {
                Text(
                    text = expense.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = SteelMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = formatMoney(expense.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = SteelMuted,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = ExpenseDateUtils.timeLabel(expense.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = SteelMuted,
                )
            }
        }
        if (confirmDelete) {
            TextButton(
                onClick = { onDelete(expense) },
            ) {
                Text(
                    text = "TAP TO CONFIRM",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        } else {
            IconButton(onClick = { confirmDelete = true }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete expense",
                    tint = SteelMuted,
                )
            }
        }
    }
}

@Composable
private fun EmptyMonthState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SteelCard)
                .border(1.dp, SteelBorder, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                contentDescription = null,
                tint = SteelMuted,
                modifier = Modifier.size(30.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No expenses this month",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tap + to record your first expense",
            style = MaterialTheme.typography.bodyMedium,
            color = SteelMuted,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    onDismiss: () -> Unit,
    onSave: (amount: Double, category: String, note: String, timestamp: Long) -> Unit,
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.all.first()) }
    var note by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SteelCard,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "New Expense",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(20.dp))

            val parsedAmount = amountText.toDoubleOrNull()
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    amountText = input
                        .replace(Regex("[^0-9.]"), "")
                        .replace(Regex("\\.(?=.*\\.)"), "")
                        .take(12)
                },
                label = { Text("Amount") },
                prefix = { Text("₱", style = MaterialTheme.typography.titleMedium) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountText.isNotEmpty() && (parsedAmount == null || parsedAmount <= 0.0),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp,
                color = SteelMuted,
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ExpenseCategory.all.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it.take(80) },
                label = { Text("Note (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "DATE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = SteelMuted,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = { showDatePicker = true }) {
                    Text(ExpenseDateUtils.shortDateLabel(dateMillis))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                enabled = parsedAmount != null && parsedAmount > 0.0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color(0xFF052E16),
                ),
                onClick = {
                    val value = amountText.toDoubleOrNull() ?: return@Button
                    onSave(value, selectedCategory, note.trim(), dateMillis)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "Add Expense",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            dateMillis = ExpenseDateUtils.localNoonFromUtcMidnight(utcMillis)
                        }
                    },
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
