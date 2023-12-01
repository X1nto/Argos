package dev.xinto.argos.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

@OptIn(ExperimentalForeignApi::class)
actual fun Int.formatCurrency(currency: String): String {
    return NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        currencyCode = currency
    }.stringFromNumber(this.convert())!!
}