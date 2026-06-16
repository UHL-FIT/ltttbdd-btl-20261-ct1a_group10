package com.example.qlct.repository

import com.example.qlct.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepository {

    private val db = FirebaseFirestore.getInstance()

    fun addTransaction(
        userId: String,
        transaction: Transaction,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        if (userId.isEmpty()) {
            onFail("User ID is empty")
            return
        }

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .add(transaction)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFail(it.message ?: "Lỗi khi thêm giao dịch")
            }
    }

    fun getTransactions(
        userId: String,
        onResult: (List<Transaction>) -> Unit
    ) {
        if (userId.isEmpty()) {
            onResult(emptyList())
            return
        }

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val list = mutableListOf<Transaction>()

                value?.documents?.forEach { doc ->

                    val transaction =
                        doc.toObject(Transaction::class.java)

                    if (transaction != null) {

                        list.add(
                            transaction.copy(
                                id = doc.id
                            )
                        )
                    }
                }

                onResult(list)
            }
    }

    fun deleteTransaction(
        userId: String,
        transactionId: String,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        if (userId.isEmpty()) {
            onFail("User ID is empty")
            return
        }

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .document(transactionId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFail(it.message ?: "Lỗi khi xoá")
            }
    }

    fun updateTransaction(
        userId: String,
        transaction: Transaction,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        if (userId.isEmpty() || transaction.id.isEmpty()) {
            onFail("User ID or Transaction ID is empty")
            return
        }

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .document(transaction.id)
            .set(transaction)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFail(it.message ?: "Lỗi khi cập nhật")
            }
    }

    fun saveCategories(
        userId: String,
        type: String, // "income" or "expense"
        categories: List<String>,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        if (userId.isEmpty()) return

        db.collection("users")
            .document(userId)
            .collection("settings")
            .document("categories_$type")
            .set(mapOf("list" to categories))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFail(it.message ?: "Lỗi khi lưu danh mục") }
    }

    fun getCategories(
        userId: String,
        type: String,
        onResult: (List<String>) -> Unit
    ) {
        if (userId.isEmpty()) {
            onResult(emptyList())
            return
        }

        db.collection("users")
            .document(userId)
            .collection("settings")
            .document("categories_$type")
            .get()
            .addOnSuccessListener { doc ->
                val list = doc.get("list") as? List<String>
                onResult(list ?: emptyList())
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}