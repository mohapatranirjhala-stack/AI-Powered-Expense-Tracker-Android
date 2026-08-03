package com.example.expensetracker.ai

import com.example.expensetracker.Expense

object FinancialPromptBuilder {

    fun buildPrompt(
        expenses: List<Expense>,
        monthlyBudget: Int,
        userQuestion: String
    ): String {

        val total =
            expenses.sumOf {
                it.amount.toIntOrNull() ?: 0
            }

        val remaining =
            monthlyBudget - total

        val expenseDetails =
            StringBuilder()

        expenses.forEach {

            expenseDetails.append(
                "- ${it.title}: ₹${it.amount} on ${it.date}\n"
            )
        }

        return """
You are an expert AI Financial Advisor.

Analyze the user's expenses carefully.

Monthly Budget:
₹$monthlyBudget

Total Spent:
₹$total

Remaining Budget:
₹$remaining

Expense History:

$expenseDetails

User Question:

$userQuestion

Give practical financial advice.

If spending is high,
suggest ways to save money.

If spending is balanced,
encourage good habits.

Answer in simple English.

Do NOT use Markdown.
Do NOT use ** or * symbols.
Return plain text only.
Use short paragraphs and numbered points.

Limit the response to about 120 words.
        """.trimIndent()
    }
}