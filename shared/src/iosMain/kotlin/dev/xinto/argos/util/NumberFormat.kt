package dev.xinto.argos.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual fun Int.formatCurrency(currency: String): String {
    return NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        currencyCode = currency
    }.stringFromNumber(NSNumber(this))!!
}