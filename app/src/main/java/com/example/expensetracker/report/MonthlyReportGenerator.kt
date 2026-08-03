package com.example.expensetracker.report

import com.example.expensetracker.Expense

object MonthlyReportGenerator {

    fun generateReport(
        expenses: List<Expense>,
        monthlyBudget: Int
    ): String {

        if (expenses.isEmpty()) {
            return """
📊 Monthly Financial Report

No expenses were recorded this month.

Start adding expenses to receive personalized financial reports.
            """.trimIndent()
        }

        val totalSpent =
            expenses.sumOf {
                it.amount.toIntOrNull() ?: 0
            }

        val transactionCount =
            expenses.size

        val averageExpense =
            totalSpent / transactionCount

        val budgetUsed =
            (totalSpent * 100) / monthlyBudget

        val remaining =
            monthlyBudget - totalSpent
        val categoryTotals =
            HashMap<String, Int>()

        expenses.forEach {

            val amount =
                it.amount.toIntOrNull() ?: 0

            categoryTotals[it.title] =
                categoryTotals.getOrDefault(
                    it.title,
                    0
                ) + amount
        }

        val topCategory =
            categoryTotals.maxByOrNull {
                it.value
            }

        val highestCategory =
            topCategory?.key ?: "None"

        val highestAmount =
            topCategory?.value ?: 0

        val status =
            when {

                budgetUsed >= 100 ->
                    "Budget Exceeded"

                budgetUsed >= 90 ->
                    "Critical"

                budgetUsed >= 70 ->
                    "Warning"

                else ->
                    "Healthy"
            }

        val recommendation =
            when {

                budgetUsed >= 100 ->
                    "You have exceeded your monthly budget. Reduce discretionary expenses and prioritize essential spending."

                budgetUsed >= 90 ->
                    "You are very close to your monthly budget. Avoid unnecessary purchases for the rest of the month."

                highestAmount > totalSpent * 0.40 ->
                    "Most of your spending is on $highestCategory. Consider setting a spending limit for this category."

                else ->
                    "Your spending is balanced. Continue tracking expenses regularly and maintain your current financial habits."
            }
        return """
📊 Monthly Financial Report

💰 Total Spending: ₹$totalSpent

🎯 Monthly Budget: ₹$monthlyBudget

💵 Remaining Budget: ₹$remaining

📈 Budget Used: $budgetUsed%

🧾 Total Transactions: $transactionCount

📊 Average Expense: ₹$averageExpense

🏆 Highest Spending Category:
$highestCategory (₹$highestAmount)

📌 Financial Status:
$status

💡 Recommendation:

$recommendation
""".trimIndent()
    }
}