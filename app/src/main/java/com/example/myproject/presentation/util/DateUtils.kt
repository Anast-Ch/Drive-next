package com.example.myproject.presentation.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale

object DateUtils {

    private val datePattern = Regex("[0-9]{2}/[0-9]{2}/[0-9]{4}")

    // Пользователь видит dd/MM/yyyy.
    // Для строгого разбора в java.time используем uuuu вместо yyyy.
    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT)

    // Дата из MaterialDatePicker → текст в поле.
    fun formatUtcMillis(utcMillis: Long): String {
        return Instant.ofEpochMilli(utcMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .format(formatter)
    }

    // Текст → настоящая календарная дата.
    // Неверный формат или несуществующая дата возвращают null.
    fun parse(text: String): LocalDate? {
        val value = text.trim()

        if (!datePattern.matches(value)) {
            return null
        }

        return try {
            LocalDate.parse(value, formatter)
                .takeIf { date -> date.year > 0 }
        } catch (_: DateTimeParseException) {
            null
        }
    }

    // Дата → UTC-миллисекунды для MaterialDatePicker.
    fun toUtcMillis(date: LocalDate): Long {
        return date.atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }
}
