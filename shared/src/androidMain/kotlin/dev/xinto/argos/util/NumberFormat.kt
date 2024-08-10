package dev.xinto.argos.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


actual fun Int.formatCurrency(currency: String): String {
    val georgianLocale = Locale.forLanguageTag("ka")
    return NumberFormat.getCurrencyInstance(georgianLocale).apply {
        setCurrency(Currency.getInstance(currency))
    }.format(BigDecimal(this).movePointLeft(2))
}