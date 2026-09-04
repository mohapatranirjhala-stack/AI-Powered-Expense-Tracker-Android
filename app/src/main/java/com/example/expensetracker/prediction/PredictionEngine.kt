package com.example.expensetracker.prediction

import com.example.expensetracker.Expense
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object PredictionEngine {

    data class PredictionResult(
        val predictedAmount: Int,
        val message: String
    )

    fun predictMonthlyExpense(
        expenses: List<Expense>,
        monthlyBudget: Int
    ): PredictionResult {

        val calendar = Calendar.getInstance()

        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val totalDays =
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val currentDay =
            calendar.get(Calendar.DAY_OF_MONTH)

        var currentMonthSpent = 0

        expenses.forEach { expense ->

            val date = parseDate(expense.date)

            if (date != null) {

                val expenseCalendar = Calendar.getInstance()
                expenseCalendar.time = date

                if (
                    expenseCalendar.get(Calendar.MONTH) == currentMonth &&
                    expenseCalendar.get(Calendar.YEAR) == currentYear
                ) {

                    currentMonthSpent +=
                        expense.amount.toIntOrNull() ?: 0
                }
            }
        }

        // No current-month expenses
        if (currentMonthSpent == 0) {

            return PredictionResult(
                0,
                "No expenses found for this month. Start adding expenses to enable prediction."
            )
        }

        // Rule-based prediction
        val averagePerDay =
            currentMonthSpent.toDouble() / currentDay

        val predicted =
            (averagePerDay * totalDays).toInt()

        val message = when {

            predicted > monthlyBudget ->
                "⚠ You may exceed your budget by ₹${predicted - monthlyBudget}. Try reducing non-essential spending."

            predicted > monthlyBudget * 0.9 ->
                "📈 You're approaching your monthly budget. Spend carefully."

            else ->
                "✅ Excellent! You are likely to stay within your monthly budget."
        }

        return PredictionResult(
            predicted,
            message
        )
    }

    private fun parseDate(dateString: String): Date? {

        // Handle Android's "Sept" format
        val normalizedDate =
            dateString
                .replace("Sept", "Sep", ignoreCase = true)

        val formats = listOf(
            "dd MMM yy",
            "dd MMM yyyy",
            "dd MMMM yy",
            "dd MMMM yyyy",
            "dd/MM/yyyy",
            "dd/MM/yy",
            "dd-MM-yyyy",
            "dd-MM-yy",
            "yyyy-MM-dd"
        )

        for (pattern in formats) {

            try {

                val format =
                    SimpleDateFormat(
                        pattern,
                        Locale.ENGLISH
                    )

                format.isLenient = false

                val parsed =
                    format.parse(normalizedDate)

                if (parsed != null) {
                    return parsed
                }

            } catch (_: Exception) {
                // Try next format
            }
        }

        return null
    }
}