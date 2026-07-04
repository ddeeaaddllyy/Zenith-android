package com.ddeeaaddllyy.zenith.ui.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

private val dayMonthFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))

fun LocalDate.toFriendlyLabel(): String {
    val today = LocalDate.now()
    return when (this) {
        today -> "Сегодня"
        today.minusDays(1) -> "Вчера"
        today.plusDays(1) -> "Завтра"
        else -> {
            val text = format(dayMonthFormatter)
            text.replaceFirstChar { it.titlecase(Locale("ru")) }
        }
    }
}

fun LocalDate.weekdayShort(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).replaceFirstChar { it.uppercase() }

fun Double.formatWeight(): String {
    val rounded = (this * 10).roundToInt() / 10.0
    return if (rounded == rounded.toLong().toDouble()) {
        "${rounded.toLong()} кг"
    } else {
        "$rounded кг"
    }
}

fun Double.formatWeightDelta(): String {
    val rounded = (this * 10).roundToInt() / 10.0
    val sign = if (rounded > 0) "+" else if (rounded < 0) "−" else ""
    val magnitude = abs(rounded)
    val text = if (magnitude == magnitude.toLong().toDouble()) {
        magnitude.toLong().toString()
    } else {
        magnitude.toString()
    }
    return "$sign$text кг"
}

fun Int.formatKcal(): String = "$this ккал"
