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

        val today = Calendar.getInstance()

        val currentMonth =
            today.get(Calendar.MONTH)

        val currentYear =
            today.get(Calendar.YEAR)

        val currentDay =
            today.get(Calendar.DAY_OF_MONTH)

        val totalDays =
            today.getActualMaximum(Calendar.DAY_OF_MONTH)

        /*
         * IMPORTANT:
         * Only expenses belonging to the current
         * calendar month and current year are used.
         */
        val currentMonthExpenses =
            expenses.filter { expense ->

                val date =
                    parseDate(expense.date)

                if (date == null) {
                    false
                } else {

                    val expenseCalendar =
                        Calendar.getInstance()

                    expenseCalendar.time = date

                    expenseCalendar.get(Calendar.MONTH) ==
                            currentMonth &&
                            expenseCalendar.get(Calendar.YEAR) ==
                            currentYear
                }
            }

        /*
         * Calculate actual spending for the
         * current month only.
         */
        val currentMonthSpent =
            currentMonthExpenses.sumOf {

                it.amount.toIntOrNull() ?: 0
            }

        /*
         * No current-month expenses.
         */
        if (currentMonthSpent <= 0) {

            return PredictionResult(
                0,
                "No expenses found for this month. Start adding expenses to enable prediction."
            )
        }

        /*
         * Rule-based monthly projection:
         *
         * Average spending per day
         * =
         * current-month spending / days elapsed
         *
         * Predicted monthly spending
         * =
         * average daily spending × total days
         */
        val averagePerDay =
            currentMonthSpent.toDouble() /
                    currentDay.coerceAtLeast(1)

        val predicted =
            (
                    averagePerDay * totalDays
                    ).toInt()

        /*
         * Never allow prediction to be below
         * what has already been spent.
         */
        val finalPrediction =
            maxOf(
                predicted,
                currentMonthSpent
            )

        val message =
            when {

                finalPrediction > monthlyBudget -> {

                    "⚠ You may exceed your budget by ₹" +
                            "${finalPrediction - monthlyBudget}. " +
                            "Try reducing non-essential spending."
                }

                finalPrediction >
                        monthlyBudget * 0.9 -> {

                    "📈 You're approaching your monthly budget. " +
                            "Spend carefully."
                }

                else -> {

                    "✅ Excellent! You are likely to stay " +
                            "within your monthly budget."
                }
            }

        return PredictionResult(
            finalPrediction,
            message
        )
    }

    private fun parseDate(
        dateString: String
    ): Date? {

        /*
         * Android may save September as "Sept"
         * while SimpleDateFormat uses "Sep".
         */
        val normalizedDate =
            dateString
                .trim()
                .replace(
                    "Sept",
                    "Sep",
                    ignoreCase = true
                )

        val formats =
            listOf(
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

                val formatter =
                    SimpleDateFormat(
                        pattern,
                        Locale.ENGLISH
                    )

                formatter.isLenient = false

                val parsed =
                    formatter.parse(normalizedDate)

                if (parsed != null) {
                    return parsed
                }

            } catch (_: Exception) {
                // Try the next format
            }
        }

        return null
    }
}