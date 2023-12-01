package dev.xinto.argos.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


actual fun Int.formatCurrency(currency: String): String {
    return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("ka"))
        .apply {
            setCurrency(Currency.getInstance(currency))
        }.format(
            BigDecimal(this)
                .movePointLeft(2)
        )
}