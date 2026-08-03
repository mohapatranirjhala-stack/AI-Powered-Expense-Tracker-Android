package com.example.expensetracker.utils

import com.example.expensetracker.Expense

object ExpenseAnalyzer {

    fun getTotalExpense(expenses: List<Expense>): Int {
        return expenses.sumOf {
            it.amount.toIntOrNull() ?: 0
        }
    }

    fun getHighestExpense(expenses: List<Expense>): Int {
        return expenses.maxOfOrNull {
            it.amount.toIntOrNull() ?: 0
        } ?: 0
    }

    fun getAverageExpense(expenses: List<Expense>): Int {

        if (expenses.isEmpty())
            return 0

        return getTotalExpense(expenses) / expenses.size
    }

    fun getTransactionCount(expenses: List<Expense>): Int {
        return expenses.size
    }

    fun getTopCategory(expenses: List<Expense>): String {

        if (expenses.isEmpty())
            return "No Data"

        val categoryMap = HashMap<String, Int>()

        expenses.forEach {

            val amount =
                it.amount.toIntOrNull() ?: 0

            categoryMap[it.title] =
                categoryMap.getOrDefault(
                    it.title,
                    0
                ) + amount
        }

        return categoryMap.maxByOrNull {
            it.value
        }?.key ?: "Unknown"
    }

    fun generateSmartInsight(
        expenses: List<Expense>,
        monthlyBudget: Int
    ): String {

        if (expenses.isEmpty()) {
            return "No expenses available."
        }

        val total = getTotalExpense(expenses)

        val categoryMap = HashMap<String, Int>()

        expenses.forEach {

            val amount =
                it.amount.toIntOrNull() ?: 0

            categoryMap[it.title] =
                categoryMap.getOrDefault(
                    it.title,
                    0
                ) + amount
        }

        val highestCategory =
            categoryMap.maxByOrNull {
                it.value
            }

        val topName =
            highestCategory?.key ?: "Unknown"

        val topAmount =
            highestCategory?.value ?: 0

        val percentage =
            if (total == 0)
                0
            else
                ((topAmount.toFloat() / total) * 100).toInt()

        val remaining =
            monthlyBudget - total

        return when {

            total > monthlyBudget ->
                "⚠ Budget exceeded by ₹${total - monthlyBudget}\n\n🍽 Highest spending: $topName ($percentage%)"

            percentage >= 50 ->
                "💡 $topName accounts for $percentage% of your spending.\n\n💰 Remaining Budget: ₹$remaining"

            percentage >= 30 ->
                "📊 Most spending is on $topName ($percentage%).\n\n💰 Remaining Budget: ₹$remaining"

            else ->
                "✅ Spending looks balanced.\n\n💰 Remaining Budget: ₹$remaining"
        }
    }
}