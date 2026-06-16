package com.example.qlct.util

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.NumberFormat
import java.util.Locale

enum class CurrencyFormat(val locale: Locale, val displayName: String) {
    VIETNAM(Locale("vi", "VN"), "VNĐ (₫)"),
    US(Locale.US, "USD ($)"),
    EUROPE(Locale.FRANCE, "EUR (€)"),
    JAPAN(Locale.JAPAN, "JPY (¥)")
}

object CurrencyPrefs {
    private const val PREFS_NAME = "currency_prefs"
    private const val KEY_FORMAT = "current_format"

    var currentFormat by mutableStateOf(CurrencyFormat.VIETNAM)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedFormat = prefs.getString(KEY_FORMAT, CurrencyFormat.VIETNAM.name)
        currentFormat = try {
            CurrencyFormat.valueOf(savedFormat ?: CurrencyFormat.VIETNAM.name)
        } catch (e: Exception) {
            CurrencyFormat.VIETNAM
        }
    }

    fun saveFormat(context: Context, format: CurrencyFormat) {
        currentFormat = format
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FORMAT, format.name).apply()
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(CurrencyPrefs.currentFormat.locale)
    return format.format(amount)
}
