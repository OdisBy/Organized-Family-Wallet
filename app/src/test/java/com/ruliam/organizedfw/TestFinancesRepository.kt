package com.ruliam.organizedfw

//class TestFinancesRepository : FinanceRepository {
//
//    val financeList = mutableListOf<FinanceEntity>()
//
//    override fun fetchFinances() = financeList
//
//    // Expenses
//    override fun addNewFinance(
//        name: String,
//        type: TypesOfExpense,
//        date: Calendar,
//        amount: Double
//    ) {
//        financeList.add(
//            Expense(
//                id = UUID.randomUUID(),
//                name = name,
//                type = type,
//                date = date,
//                amount = amount
//            )
//        )
//    }
//
//    // Budgets
//    override fun addNewFinance(
//        name: String,
//        type: TypesOfBudget,
//        date: kotlinx.datetime.LocalDate,
//        amount: Double
//    ) {
//        financeList.add(
//            Budget(
//                id = UUID.randomUUID(),
//                name = name,
//                type = type,
//                date = date,
//                amount = amount
//            )
//        )
//    }
//
//}