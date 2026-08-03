package com.example.expensetracker.prediction

import com.example.expensetracker.Expense
import java.text.SimpleDateFormat
import java.util.*

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

        val currentMonth =
            calendar.get(Calendar.MONTH)

        val currentYear =
            calendar.get(Calendar.YEAR)

        val totalDays =
            calendar.getActualMaximum(
                Calendar.DAY_OF_MONTH
            )

        val currentDay =
            calendar.get(Calendar.DAY_OF_MONTH)

        val formats = listOf(

            SimpleDateFormat(
                "dd MMM yy",
                Locale.ENGLISH
            ),

            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.ENGLISH
            ),

            SimpleDateFormat(
                "dd MMMM yy",
                Locale.ENGLISH
            ),

            SimpleDateFormat(
                "dd MMMM yyyy",
                Locale.ENGLISH
            )
        )

        var currentMonthSpent = 0

        expenses.forEach { expense ->

            var date: Date? = null

            for (format in formats) {

                try {

                    date = format.parse(expense.date)

                    if (date != null)
                        break

                } catch (_: Exception) {
                }
            }

            if (date != null) {

                val expenseCalendar =
                    Calendar.getInstance()

                expenseCalendar.time = date

                if (
                    expenseCalendar.get(Calendar.MONTH)
                    == currentMonth
                    &&
                    expenseCalendar.get(Calendar.YEAR)
                    == currentYear
                ) {

                    currentMonthSpent +=
                        expense.amount.toIntOrNull()
                            ?: 0
                }
            }
        }

        if (currentMonthSpent == 0) {

            return PredictionResult(

                0,

                "No expenses found for this month. Start adding expenses to enable prediction."
            )
        }

        val averagePerDay =
            currentMonthSpent.toDouble() /
                    currentDay

        val predicted =
            (averagePerDay * totalDays)
                .toInt()

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
}