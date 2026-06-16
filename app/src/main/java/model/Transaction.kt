package com.example.qlct.model

data class Transaction(

    val id: String = "",

    val type: String = "",

    val amount: Double = 0.0,

    val category: String = "",

    val note: String = "",

    val date: Long = System.currentTimeMillis()

)