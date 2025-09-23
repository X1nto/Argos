package dev.xinto.argos.util

import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual fun Int.formatCurrency(currency: String): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        currencyCode = currency
    }
    val cents = NSDecimalNumber(mantissa = this.toULong(), exponent = -2, isNegative = false)
    return formatter.stringFromNumber(cents)!!
}