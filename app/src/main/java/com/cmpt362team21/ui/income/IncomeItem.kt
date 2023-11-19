package com.cmpt362team21.ui.income

// IncomeItem.kt
data class IncomeItem(val incomeType: String, val incomeAmount: String)

val incomeItems = listOf(
    IncomeItem("Salary", "$3,000"),
    IncomeItem("Freelance", "$1,000"),
    IncomeItem("Rent", "$4,500"),
    // Add more items as needed
)
