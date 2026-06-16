package com.example.qlct.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.qlct.model.Transaction
import com.example.qlct.repository.TransactionRepository

class TransactionViewModel : ViewModel() {

    private val repo = TransactionRepository()

    var transactions = mutableStateListOf<Transaction>()
        private set

    var incomeCategories = mutableStateListOf<String>("Lương", "Thưởng", "Kinh doanh", "Đầu tư")
        private set

    var expenseCategories = mutableStateListOf<String>("Ăn uống", "Di chuyển", "Mua sắm", "Giải trí", "Nhà cửa", "Sức khỏe", "Giáo dục")
        private set

    fun addIncome(
        userId: String,
        amount: Double,
        category: String,
        note: String,
        date: Long
    ) {

        val transaction = Transaction(
            type = "income",
            amount = amount,
            category = category,
            note = note,
            date = date
        )

        repo.addTransaction(
            userId = userId,
            transaction = transaction,
            onSuccess = {},
            onFail = {}
        )
    }

    fun addExpense(
        userId: String,
        amount: Double,
        category: String,
        note: String,
        date: Long
    ) {

        val transaction = Transaction(
            type = "expense",
            amount = amount,
            category = category,
            note = note,
            date = date
        )

        repo.addTransaction(
            userId = userId,
            transaction = transaction,
            onSuccess = {},
            onFail = {}
        )
    }

    fun loadTransactions(userId: String) {

        repo.getTransactions(userId) { list ->

            transactions.clear()

            transactions.addAll(list)
        }
    }

    fun getTotalIncome(): Double {

        return transactions
            .filter { it.type == "income" }
            .sumOf { it.amount }
    }

    fun getTotalExpense(): Double {

        return transactions
            .filter { it.type == "expense" }
            .sumOf { it.amount }
    }

    fun getBalance(): Double {

        return getTotalIncome() - getTotalExpense()
    }

    fun deleteTransaction(userId: String, transactionId: String) {
        repo.deleteTransaction(
            userId = userId,
            transactionId = transactionId,
            onSuccess = {},
            onFail = {}
        )
    }

    fun updateTransaction(userId: String, transaction: Transaction) {
        repo.updateTransaction(
            userId = userId,
            transaction = transaction,
            onSuccess = {},
            onFail = {}
        )
    }

    fun loadCategories(userId: String) {
        if (userId.isEmpty()) return
        repo.getCategories(userId, "income") { list ->
            if (list.isNotEmpty()) {
                incomeCategories.clear()
                incomeCategories.addAll(list)
            }
        }
        repo.getCategories(userId, "expense") { list ->
            if (list.isNotEmpty()) {
                expenseCategories.clear()
                expenseCategories.addAll(list)
            }
        }
    }

    fun addCategory(userId: String, type: String, category: String) {
        if (userId.isEmpty()) return
        if (type == "income") {
            if (!incomeCategories.contains(category)) {
                incomeCategories.add(category)
                repo.saveCategories(userId, "income", incomeCategories.toList(), {}, {})
            }
        } else {
            if (!expenseCategories.contains(category)) {
                expenseCategories.add(category)
                repo.saveCategories(userId, "expense", expenseCategories.toList(), {}, {})
            }
        }
    }

    fun removeCategory(userId: String, type: String, category: String) {
        if (userId.isEmpty()) return
        if (type == "income") {
            incomeCategories.remove(category)
            repo.saveCategories(userId, "income", incomeCategories.toList(), {}, {})
        } else {
            expenseCategories.remove(category)
            repo.saveCategories(userId, "expense", expenseCategories.toList(), {}, {})
        }
    }

    fun addTransaction(userId: String, transaction: Transaction) {
        repo.addTransaction(
            userId = userId,
            transaction = transaction,
            onSuccess = {},
            onFail = {}
        )
    }
}
    