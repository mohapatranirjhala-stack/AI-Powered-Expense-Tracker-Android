package com.example.expensetracker

data class SavingsGoal(
    var id: String = "",
    var goalName: String = "",
    var targetAmount: Double = 0.0,
    var currentAmount: Double = 0.0,
    var targetDate: String = "",
    var createdAt: Long = System.currentTimeMillis()
)